from flask import Flask, request, jsonify, send_from_directory
import mysql.connector
import logging
from werkzeug.utils import secure_filename
import os

logging.basicConfig(level=logging.DEBUG)

mysql_config = {
    'user': 'admin',
    'password': 'admin123',
    'host': 'db',  # O el host donde se encuentra tu servidor MySQL
    'port': 3306,
    'database': 'calistenigram',
    'raise_on_warnings': True
}

app = Flask(__name__)

# Define a route for your API endpoint
@app.route('/api/data', methods=['GET'])
def get_data():
    # Dummy data for demonstration
    data = {'message': 'Hello from the server!'}
    return jsonify(data)

@app.route('/create_user', methods=['POST'])
def create_user(): # { "email": str, "token": str}
    if request.is_json:
        # Get the JSON data
            data = request.get_json()
            app.logger.debug(data)
    else:
        return jsonify({"error": "Request must be JSON"}), 400
    cnx = None
    try:
        cnx = mysql.connector.connect(**mysql_config)
        cursor = cnx.cursor()
        print("Conexión exitosa a la base de datos MySQL")
        query = "INSERT INTO Users (email, token) VALUES (%s, %s)"
        values = (data['email'],data['token'])
        cursor.execute(query, values)
        cnx.commit()
        return jsonify({"message": "User created successfully"}), 201
    except mysql.connector.Error as err:
        print("Error al conectar a la base de datos MySQL:", err)
        return jsonify({"error": str(err)}), 500
    finally:
        if cnx:
            cnx.close()

@app.route('/upload_img', methods=['POST'])
def upload_user_image(): # { "email": str } files: image
    email = request.form.get('email')
    image = request.files.get('image')
    
    if not email or not image:
        return jsonify({"error": "Missing email or image"}), 400

    # Ensure the 'images' directory exists
    image_directory = os.path.join('.', 'images')
    if not os.path.exists(image_directory):
        os.makedirs(image_directory)
    filename = secure_filename(image.filename)
    image.save(os.path.join(image_directory, filename))  # Save the file to a directory on your server

    cnx = None
    try:
        cnx = mysql.connector.connect(**mysql_config)
        cursor = cnx.cursor()
        query = "INSERT INTO Images (direccion, email, likes) VALUES (%s, %s, %s)"
        values = (filename, email, 0)  # Store the filename as a reference in the database
        cursor.execute(query, values)
        cnx.commit()
        return jsonify({"message": "User and image uploaded successfully"}), 201
    except mysql.connector.Error as err:
        app.logger.debug("Error connecting to MySQL database:", err)
        if cnx:
            cnx.rollback()
        return jsonify({"error": str(err)}), 500
    finally:
        if cnx:
            cnx.close()

@app.route('/get_imgs', methods=['GET'])
def get_images():
    # This function returns a list of images from the database with their email, filename, and likes.
    cnx = None
    try:
        cnx = mysql.connector.connect(**mysql_config)
        cursor = cnx.cursor()
        query = "SELECT email, direccion, likes FROM Images"
        cursor.execute(query)
        result = cursor.fetchall()  # Fetch all rows of the query result
        image_list = [{'email': email, 'filename': filename, 'likes': likes} for email, filename, likes in result]
        return jsonify(image_list), 200  # Return the list of images with HTTP status 200 (OK)
    except mysql.connector.Error as err:
        app.logger.debug(f"Error connecting to MySQL database: {err}")
        return jsonify({"error": str(err)}), 500  # Return error message with HTTP status 500 (Internal Server Error)
    finally:
        if cnx:
            cnx.close()  # Ensure connection is closed in finally block

@app.route('/get_img/<filename>', methods=['GET'])
def get_image(filename):
    image_folder = './images/'
    if filename in os.listdir(image_folder) and filename.endswith('.jpg'):
        return send_from_directory(image_folder, filename)
    else:
        abort(404)  # If the file is not found, return a 404 error

@app.route('/send_like', methods=['POST'])
def send_like(): # { "filename": str }
    filename = request.json.get("filename")
    if not filename:
        return jsonify({"error": "Filename is required"}), 400  # Return error if no filename provided
    cnx = None
    try:
        cnx = mysql.connector.connect(**mysql_config)
        cursor = cnx.cursor()
        query = "UPDATE Images SET likes = likes + 1 WHERE direccion = %s"
        values = (filename,)
        cursor.execute(query, values)
        cnx.commit()
        return jsonify({"message": "Like sent successfully"}), 200  # Return the list of images with HTTP status 200 (OK)
    except mysql.connector.Error as err:
        app.logger.debug(f"Error connecting to MySQL database: {err}")
        return jsonify({"error": str(err)}), 500  # Return error message with HTTP status 500 (Internal Server Error)
    finally:
        if cnx:
            cnx.close()  # Ensure connection is closed in finally block

if __name__ == '__main__':
    app.run(debug=True, host="0.0.0.0", port=8000)
