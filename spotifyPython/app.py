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
from moviepy.editor import VideoFileClip

app = Flask(__name__)



@app.route("/testSpring", methods=['POST'])
def testSpring():

	data = request.json

	if data:
		print(f"Number of songs to process: {len(data)}")
		donwload_and_convert_songs(data)
		return "Songs processed successfully"
	else:
		return "No data provided"


def donwload_and_convert_songs(song_data):
	for artist, song_name in song_data.items():
		try:
			youtube_url = get_url_video(artist, song_name)
			print(youtube_url)
			if youtube_url:
				video_path = download_song(youtube_url)
				convert_to_mp3(video_path)
		except Exception as e:
			print(f"An error occurred while processing {song_name}: {str(e)}")



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
    yt_stream = yt.streams.filter(file_extension="mp4").get_by_resolution("360p")
    if yt_stream:
    	video_path = yt_stream.download(output_path="D:\songsVideos", filename=yt.title)
    	return video_path
    return None
    
def convert_to_mp3(video_path):
	print(f"Converting {video_path} to MP3...")
	if video_path is not None:
		video = VideoFileClip(video_path)
		mp3_path = os.path.join("D:\songsFormatMP3", os.path.splitext(os.path.basename(video_path))[0] + ".mp3")
		video.audio.write_audiofile(mp3_path)
		video.close()
	else:
		print("Video path is None. Cannot perform conversion.")

	
	
	
	

	