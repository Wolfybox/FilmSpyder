from flask import Flask, jsonify
import flask_cors

import geojson

import logging

logger = logging.getLogger(__name__)

app = Flask(__name__)
flask_cors.CORS(app)

@app.route("/")
def main():
    return '''
    hello <a href="/me">your name</a>
    list of current <a href="/tweets">tweets</a>
    '''

@app.route("/me")
def user():
    api = app.api
    name = api.me().name
    return name

@app.route("/tweets")
def tweets():
    features = []
    logger.info("returning listener queue of length %s", app.listener.status_queue)
    for status in app.listener.status_queue:
        properties = {
            "text": status.text,
            "source": status.source,
            "user": {
                "name": status.user.name,
                "id": status.user.id
            }
        }
        feature = geojson.Feature(
            id=status.id,
            geometry=status.coordinates,
            properties=properties
        )
        features.append(feature)
    feature_collection = geojson.FeatureCollection(features)
    return jsonify(feature_collection)
