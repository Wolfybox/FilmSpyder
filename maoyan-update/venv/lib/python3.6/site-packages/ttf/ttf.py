# -*- coding: utf-8 -*-

"""Main module."""

import pathlib
import json
import collections
import logging

import shapely
import geojson
import tweepy

locations = [
    # Rotterdam center
    geojson.Point([4.467, 51.917]),
    geojson.Point([4.3780269, 51.9856484])
]

logger = logging.getLogger(__name__)
logger.setLevel(logging.DEBUG)


class QueuedListener(tweepy.StreamListener):
    def __init__(self, *args, **kwargs):
        super().__init__(*args, **kwargs)
        self.status_queue = collections.deque(maxlen=100)

    def on_status(self, status):
        logger.info(
            "got new message: id: %s, text: %s, geo: %s, %s messages in queue",
            status.id,
            status.text,
            status.coordinates,
            len(self.status_queue)
        )
        if status.coordinates is None:
            return
        self.status_queue.append(status)


def login(config_path="~/.twitter.json", app="ttf"):
    """read the config_path, log into twitter and return the api"""

    # define 2 functions needed to login
    def get_keys(config_path):
        key_path = pathlib.Path(config_path).expanduser()
        with key_path.open() as f:
            data = json.load(f)
        keys = data[app]
        return keys

    def get_api(keys):
        auth = tweepy.OAuthHandler(
            keys['consumer_key'],
            keys['consumer_secret']
        )
        auth.set_access_token(keys['access_key'], keys['access_secret'])
        api = tweepy.API(auth)
        return api

    # read config
    keys = get_keys(config_path)
    # log in
    api = get_api(keys)
    return api
