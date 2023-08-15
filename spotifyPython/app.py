from flask import Flask, request, url_for, session, redirect
import spotipy
from spotipy.oauth2 import SpotifyOAuth
import time
from pytube import YouTube
import urllib.request
import re
from urllib.parse import urlencode
import os
from yt_dlp import YoutubeDL
from youtube_dl import YoutubeDL

app = Flask(__name__)



@app.route("/testSpring", methods=['POST'])
def testSpring():

	data = request.json

	for artist, song_name in data.items():
		youtube_url = get_url_video(artist, song_name)
		print(youtube_url)
		if youtube_url:
			download_song(youtube_url)


	return "Songs processed successfully"

def get_url_video(artist, song_name):
	query = f"{artist}{song_name}"
	encoded_query = urlencode({'search_query': query})
	search_url = f"https://www.youtube.com/results?{encoded_query}"

	html = urllib.request.urlopen(search_url)
	video_ids = re.findall(r"watch\?v=(\S{11})", html.read().decode())

	if video_ids:
		return f"https://www.youtube.com/watch?v={video_ids[0]}"
	else:
		return None
	



def download_song(youtube_url):
    yt = YouTube(youtube_url)
    yt.streams.filter(file_extension="mp4").get_by_resolution("360p").download("D:\songsVideos")

	