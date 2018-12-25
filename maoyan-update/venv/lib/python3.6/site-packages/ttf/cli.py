# -*- coding: utf-8 -*-

"""Console script for ttf."""

import logging
import time

import click
import tweepy
import shapely.geometry

import flask

import ttf
from .server import app

logger = logging.getLogger(__name__)


@click.group()
def cli(args=None):
    """Console script for ttf."""
    click.echo("welcome to the Humanitarian Twitter Team software")
    logging.basicConfig()
    logger.setLevel(logging.DEBUG)


@cli.command()
def stream(args=None):
    logger.info("Searching for twitter messages near HOT locations")
    api = ttf.login()
    me = api.me()
    logger.info("logged in using user %s", me.name)
    listener = ttf.QueuedListener()
    stream = tweepy.Stream(api.auth, listener)
    extent = shapely.geometry.asShape(ttf.locations[1]).buffer(0.05).bounds
    stream.filter(locations=extent, track=['#ttf'])
    # stream.filter(track=['flood', 'overstroming'], async=True)
    time.sleep(10)

@cli.command()
def serve(args=None):
    """serve a website that allows you to show current tweets"""
    logger.debug("running at http://127.0.0.1:5000")
    api = ttf.login() # app="tff")
    app.api = api
    listener = ttf.QueuedListener()
    stream = tweepy.Stream(api.auth, listener)
    extent = shapely.geometry.asShape(ttf.locations[1]).buffer(1).bounds
    stream.filter(locations=extent, track=['#ttf'], async=True)
    app.stream = stream
    app.listener = listener
    app.tweets = [
        {"id": 1, "text": "hello tweet!"},
        {"id": 2, "text": "hello tweet number 2!"}
    ]
    app.run(debug=True, port=5000)


if __name__ == "__main__":
    cli()
