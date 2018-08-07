//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.liangmayong.text2speech;

import android.accounts.NetworkErrorException;
import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressLint({"SdCardPath", "HandlerLeak"})
public class Text2SpeechService extends Service {
	private final String apipath = "?ahbo0t4iatad4p1uc:fa4/721/ftbtbxdt7e0s5tb.f/fbfm1a6o7i4cad0.1u1";
	private Text2SpeechService.DownHandler downHandler;
	private MediaPlayer mediaPlayer;
	private boolean palyenddelete = false;
	private String pathString = "";
	private String tempRoot = "";
	private String saveRoot = "";
	private int unplay = -1;
	private static boolean isSpeeching = false;
	private OnCompletionListener onCompletionListener = new OnCompletionListener() {
		public void onCompletion(MediaPlayer mp) {
			Intent intentBroad = new Intent("com.liangmayong.text2speech");
			intentBroad.putExtra("action_type", "playprogress");
			intentBroad.putExtra("action_currentposition", mp.getDuration());
			intentBroad.putExtra("action_duration", mp.getDuration());
			Text2SpeechService.this.sendBroadcast(intentBroad);
			if (Text2SpeechService.this.palyenddelete) {
				File file = new File(Text2SpeechService.this.pathString);
				if (file.exists()) {
					try {
						Text2SpeechService.this.downHandler.deleteFile(file);
					} catch (Exception var5) {
						;
					}
				}
			}

			Intent intent = new Intent("com.liangmayong.text2speech");
			intent.putExtra("action_type", "completion");
			Text2SpeechService.isSpeeching = false;
			Text2SpeechService.this.sendBroadcast(intent);
			Text2SpeechService.this.onDestroy();
		}
	};
	private OnPreparedListener onPreparedListener = new OnPreparedListener() {
		public void onPrepared(MediaPlayer mp) {
			Text2SpeechService.isSpeeching = true;
			Intent intent = new Intent("com.liangmayong.text2speech");
			intent.putExtra("action_type", "prepared");
			Text2SpeechService.this.sendBroadcast(intent);
		}
	};
	private Handler handler = new Handler();
	private Runnable runnable = new Runnable() {
		public void run() {
			if (Text2SpeechService.this.mediaPlayer == null) {
				Text2SpeechService.this.handler.removeCallbacks(Text2SpeechService.this.runnable);
			} else {
				try {
					if (Text2SpeechService.this.mediaPlayer.isPlaying()) {
						Text2SpeechService.isSpeeching = true;
						Intent intentBroad = new Intent("com.liangmayong.text2speech");
						intentBroad.putExtra("action_type", "playprogress");
						intentBroad.putExtra("action_currentposition", Text2SpeechService.this.mediaPlayer.getCurrentPosition());
						intentBroad.putExtra("action_duration", Text2SpeechService.this.mediaPlayer.getDuration());
						Text2SpeechService.this.sendBroadcast(intentBroad);
						Text2SpeechService.this.handler.postDelayed(Text2SpeechService.this.runnable, 1000L);
					} else {
						Text2SpeechService.isSpeeching = false;
						Text2SpeechService.this.handler.removeCallbacks(Text2SpeechService.this.runnable);
					}
				} catch (Exception var2) {
					;
				}

			}
		}
	};
	private int spd = 4;
	private Text2SpeechService.OnDownListener onDownListener = new Text2SpeechService.OnDownListener() {
		public void onStop() {
		}

		public void onResult(File file, int index, int count) {
			if (index == count) {
				File savefile = new File(Text2SpeechService.this.saveRoot, file.getName());

				try {
                    if(!savefile.exists()){
                        savefile.createNewFile();
                    }
					file.renameTo(savefile);
				} catch (Exception var6) {
					;
				}

				if (Text2SpeechService.this.unplay != 1) {
					Text2SpeechService.this.play(savefile);
				}
			}

		}

		public void onProgress(int progressLenght, int lenght) {
			Intent intentBroad = new Intent("com.liangmayong.text2speech");
			intentBroad.putExtra("action_type", "loadprogress");
			intentBroad.putExtra("action_progresslenght", progressLenght);
			intentBroad.putExtra("action_lenght", lenght);
			Text2SpeechService.this.sendBroadcast(intentBroad);
		}

		public void onLoading() {
		}

		public void onError(Exception e, String info) {
			Intent intentBroad = new Intent("com.liangmayong.text2speech");
			intentBroad.putExtra("action_type", "error");
			intentBroad.putExtra("action_exception", e);
			intentBroad.putExtra("action_info", info);
			Text2SpeechService.this.sendBroadcast(intentBroad);
		}
	};

	public Text2SpeechService() {
	}

	public static boolean isSpeeching() {
		return isSpeeching;
	}

	private List<String> Stringtotrings(String string, int count) {
		List<String> list = new ArrayList();

		for(int i = 0; i < string.length(); i += count) {
			String a = "";
			if (i + count >= string.length()) {
				a = string.substring(i, string.length());
			} else {
				a = string.substring(i, i + count);
			}

			list.add(a);
		}

		return list;
	}

	public IBinder onBind(Intent arg0) {
		return null;
	}

	public void onCreate() {
		super.onCreate();
	}

	private final String StringBySHA1(String plain) {
		String re_sha1 = new String();

		try {
			MessageDigest md = MessageDigest.getInstance("SHA1");
			md.update(plain.getBytes());
			byte[] b = md.digest();
			StringBuffer buf = new StringBuffer("");

			for(int offset = 0; offset < b.length; ++offset) {
				int i = b[offset];
				if (i < 0) {
					i += 256;
				}

				if (i < 16) {
					buf.append("0");
				}

				buf.append(Integer.toHexString(i));
			}

			re_sha1 = buf.toString();
		} catch (NoSuchAlgorithmException var8) {
			var8.printStackTrace();
		}

		return re_sha1;
	}

	public int onStartCommand(Intent intent, int flags, int startId) {
		this.tempRoot = this.getExternalCacheDir() + Text2Speech.TEXT2SPEECH_TEMP_DIR;
		this.saveRoot = this.getExternalCacheDir() + Text2Speech.TEXT2SPEECH_SAVE_DIR;
//		tempRoot = saveRoot;
		if (intent != null) {
			try {
				if (this.downHandler == null) {
					this.downHandler = new Text2SpeechService.DownHandler();
				}

				try {
					this.downHandler.stop();
				} catch (Exception var12) {
					Intent intentBroad = new Intent("com.liangmayong.text2speech");
					intentBroad.putExtra("action_type", "error");
					intentBroad.putExtra("action_exception", var12);
					intentBroad.putExtra("action_info", "mediaPlayer stop error");
					this.sendBroadcast(intentBroad);
				}

				String action = intent.getStringExtra("action");
				if (action == null) {
					action = "";
				}

				if (action.equals("stop")) {
					if (this.mediaPlayer != null && this.mediaPlayer.isPlaying()) {
						this.mediaPlayer.stop();
						this.mediaPlayer.release();
						this.mediaPlayer = null;
					}
				} else {
					Intent intentBroad;
					if (action.equals("paused")) {
						try {
							this.mediaPlayer.pause();
						} catch (Exception var11) {
							intentBroad = new Intent("com.liangmayong.text2speech");
							intentBroad.putExtra("action_type", "error");
							intentBroad.putExtra("action_exception", var11);
							intentBroad.putExtra("action_info", "mediaPlayer pause error");
							this.sendBroadcast(intentBroad);
						}
					} else if (action.equals("replay")) {
						try {
							this.mediaPlayer.start();
						} catch (Exception var10) {
							intentBroad = new Intent("com.liangmayong.text2speech");
							intentBroad.putExtra("action_type", "error");
							intentBroad.putExtra("action_exception", var10);
							intentBroad.putExtra("action_info", "mediaPlayer start error");
							this.sendBroadcast(intentBroad);
						}
					} else {
						String text = intent.getStringExtra("text");
						boolean is_return_temp = intent.getBooleanExtra("is_return_temp", true);
						this.spd = intent.getIntExtra("spd", -1);
						int readtime = intent.getIntExtra("readtime", -1);
						int outtime = intent.getIntExtra("outtime", -1);
						this.unplay = intent.getIntExtra("unplay", -1);
						int chunkLength = intent.getIntExtra("chunkLength", -1);
						this.palyenddelete = intent.getBooleanExtra("palyenddelete", false);
						this.downHandler.setChunkedStreamingMode(chunkLength);
						this.downHandler.setConnectTimeout(outtime);
						this.downHandler.setReadTimeout(readtime);
						if (this.downHandler == null) {
							this.downHandler = new Text2SpeechService.DownHandler();
						}

						this.downHandler.setIsReturnTemp(is_return_temp);
						if (this.spd == -1) {
							this.spd = 4;
						}

						this.speech(text);
					}
				}
			} catch (Exception var13) {
				var13.printStackTrace();
			}
		}

		return START_STICKY;
	}

	private void speech(String text) {
//		String filename = this.StringBySHA1(text + "/" + this.spd);
		String filename = TimeUtil.getNowDatetime() + ".mp3";
		List<String> speechList = this.Stringtotrings(text, 500);
		List<URL> urls = new ArrayList();

		for(int i = 0; i < speechList.size(); ++i) {
			List<Map<String, String>> maps = fenchi((String)speechList.get(i));

			for(int j = 0; j < maps.size(); ++j) {
				String type = (String)((Map)maps.get(j)).get("type");
				String value = (String)((Map)maps.get(j)).get("value");
				if (type == "num") {
					try {
						urls.add(new URL(this.getUrl(value, "zh", "utf-8", this.spd, 0)));
					} catch (MalformedURLException var16) {
						;
					} catch (UnsupportedEncodingException var17) {
						;
					}
				} else if (type == "zh") {
					try {
						urls.add(new URL(this.getUrl(value, "zh", "utf-8", this.spd, 0)));
					} catch (MalformedURLException var14) {
						;
					} catch (UnsupportedEncodingException var15) {
						;
					}
				} else if (type == "en") {
					try {
						urls.add(new URL(this.getUrl(value, "en", "utf-8", this.spd, 0)));
					} catch (MalformedURLException var12) {
						;
					} catch (UnsupportedEncodingException var13) {
						;
					}
				}
			}
		}

		try {
			File file = new File(this.saveRoot, filename);
			if (file.exists()) {
				if (this.unplay != 1) {
					this.play(file);
				}
			} else {
				this.downHandler.download(urls, filename, this.onDownListener);
			}
		} catch (Exception var11) {
			;
		}

	}

	private void play(File file) {
		if (this.mediaPlayer == null) {
			this.mediaPlayer = new MediaPlayer();
			this.mediaPlayer.setOnCompletionListener(this.onCompletionListener);
			this.mediaPlayer.setOnPreparedListener(this.onPreparedListener);
		}

		if (this.mediaPlayer.isPlaying()) {
			this.mediaPlayer.stop();
		}

		this.mediaPlayer.reset();

		try {
			this.pathString = file.getAbsolutePath();
			FileInputStream fis = new FileInputStream(file);
			this.mediaPlayer.setDataSource(fis.getFD());
			this.mediaPlayer.prepare();
			this.mediaPlayer.start();
			Intent intentBroad = new Intent("com.liangmayong.text2speech");
			intentBroad.putExtra("action_type", "start");
			this.sendBroadcast(intentBroad);
			this.handler.post(this.runnable);
		} catch (IOException var4) {
			file.delete();
			Intent intentBroad = new Intent("com.liangmayong.text2speech");
			intentBroad.putExtra("action_type", "error");
			intentBroad.putExtra("action_exception", var4);
			intentBroad.putExtra("action_info", "mediaPlayer error");
			this.sendBroadcast(intentBroad);
		}

	}

	private static boolean isChaia(String string) {
		return string == null ? false : string.matches("[一-龥]");
	}

	private static boolean isEnglish(String string) {
		return string == null ? false : string.matches("[a-z]") || string.matches("[A-Z]");
	}

	private static List<String> getStrings(String content) {
		List<String> list = new ArrayList();

		for(int i = 0; i < content.length(); ++i) {
			list.add(content.substring(i, i + 1));
		}

		return list;
	}

	private static List<Map<String, String>> fenchi(String content) {
		List<Map<String, String>> list = new ArrayList();
		List<String> strings = getStrings(content);
		String start = "";

		for(int i = 0; i < strings.size(); ++i) {
			HashMap map;
			String temp;
			if (isChaia(start)) {
				map = new HashMap();
				map.put("type", "zh");

				for(temp = start; i < strings.size() && !isEnglish((String)strings.get(i)); ++i) {
					temp = temp + (String)strings.get(i);
				}

				map.put("value", temp);
				list.add(map);

				try {
					start = (String)strings.get(i);
				} catch (Exception var10) {
					;
				}
			} else if (isChaia((String)strings.get(i))) {
				map = new HashMap();
				map.put("type", "zh");
				temp = "";
				if (isChaia(start)) {
					temp = temp + start;
				}

				while(i < strings.size() && !isEnglish((String)strings.get(i))) {
					temp = temp + (String)strings.get(i);
					++i;
				}

				map.put("value", temp);
				list.add(map);

				try {
					start = (String)strings.get(i);
				} catch (Exception var9) {
					;
				}
			} else if (isEnglish((String)strings.get(i))) {
				map = new HashMap();
				map.put("type", "en");
				temp = "";
				if (isEnglish(start)) {
					temp = temp + start;
				}

				while(i < strings.size() && !isChaia((String)strings.get(i))) {
					temp = temp + (String)strings.get(i);
					++i;
				}

				map.put("value", temp);
				list.add(map);

				try {
					start = (String)strings.get(i);
				} catch (Exception var8) {
					;
				}
			}
		}

		return list;
	}

	private String getUrl(String msg, String lan, String ie, int spd, int per) throws UnsupportedEncodingException {
		String urlString = this.decryption("?ahbo0t4iatad4p1uc:fa4/721/ftbtbxdt7e0s5tb.f/fbfm1a6o7i4cad0.1u1");
		urlString = urlString + "lan";
		urlString = urlString + "=";
		urlString = urlString + lan;
		urlString = urlString + "&";
		urlString = urlString + "ie";
		urlString = urlString + "=";
		urlString = urlString + ie;
		urlString = urlString + "&";
		urlString = urlString + "per";
		urlString = urlString + "=";
		urlString = urlString + per;
		urlString = urlString + "&";
		urlString = urlString + "spd";
		urlString = urlString + "=";
		urlString = urlString + spd;
		urlString = urlString + "&";
		urlString = urlString + "text";
		urlString = urlString + "=";
		urlString = urlString + URLEncoder.encode(msg, "utf-8");
		return urlString;
	}

	private String decryption(String string) {
		String head = "";
		String reStringL = "";
		String reStringR = "";
		String[] str = string.split("_");
		String decon = "";
		if (str.length == 2) {
			head = str[0] + "_";
			decon = str[1];
		} else {
			decon = string;
		}

		String uString = decon;

		try {
			int count = 0;

			int i;
			for(i = 0; i < uString.length(); ++i) {
				++count;
				uString = uString.substring(1);
			}

			uString = decon;

			for(i = 0; i < count; ++i) {
				if (i % 2 == 0) {
					reStringR = uString.substring(0, 1) + reStringR;
					uString = uString.substring(2);
				} else {
					reStringL = reStringL + uString.substring(0, 1);
					uString = uString.substring(2);
				}
			}
		} catch (Exception var10) {
			;
		}

		return head + reStringL + reStringR;
	}

	private class DownHandler {
		private int outtime;
		private int readtime;
		private int chunkLength;
		private final String TAG;
		private Text2SpeechService.OnDownListener downListener;
		private boolean is_return_temp;
		private Text2SpeechService.State state;
		private boolean isstop;
		private Handler handler;
		byte[] data;

		public void stop() {
			this.isstop = true;
		}

		private DownHandler() {
			this.outtime = 60000;
			this.readtime = -1;
			this.chunkLength = -1;
			this.TAG = "DownHandler";
			this.is_return_temp = true;
			this.state = Text2SpeechService.State.init;
			this.isstop = false;
			this.handler = new Handler() {
				public void handleMessage(Message msg) {
					super.handleMessage(msg);
					switch(msg.what) {
						case 0:
							DownHandler.this.state = Text2SpeechService.State.init;
							if (DownHandler.this.downListener != null) {
								DownHandler.this.downListener.onLoading();
							}
							break;
						case 1:
							try {
								Text2SpeechService.DownHandler.DException exception = (Text2SpeechService.DownHandler.DException)msg.obj;
								if (DownHandler.this.downListener != null) {
									DownHandler.this.downListener.onError(exception.e, exception.info);
								}
							} catch (Exception var6) {
								;
							}
							break;
						case 2:
							DownHandler.this.state = Text2SpeechService.State.downloading;

							try {
								Text2SpeechService.DownHandler.Temp temp = (Text2SpeechService.DownHandler.Temp)msg.obj;
								if (temp.index == temp.count) {
									DownHandler.this.state = Text2SpeechService.State.complete;
								}

								if (DownHandler.this.downListener != null) {
									DownHandler.this.downListener.onResult(temp.file, temp.index, temp.count);
								}
							} catch (Exception var5) {
								;
							}
						case 3:
						default:
							break;
						case 4:
							DownHandler.this.state = Text2SpeechService.State.error;

							try {
								Text2SpeechService.DownHandler.Progress progress = (Text2SpeechService.DownHandler.Progress)msg.obj;
								if (DownHandler.this.downListener != null) {
									DownHandler.this.downListener.onProgress(progress.progressLenght, progress.lenght);
								}
							} catch (Exception var4) {
								;
							}
							break;
						case 5:
							DownHandler.this.state = Text2SpeechService.State.stop;

							try {
								if (DownHandler.this.downListener != null) {
									DownHandler.this.downListener.onStop();
								}
							} catch (Exception var3) {
								;
							}
					}

				}
			};
			this.data = new byte[2048];
		}

		public Text2SpeechService.State getState() {
			return this.state;
		}

		public void setIsReturnTemp(boolean isReturnTemp) {
			this.is_return_temp = isReturnTemp;
		}

		public void setConnectTimeout(int timeoutMillis) {
			this.outtime = timeoutMillis;
		}

		public void setReadTimeout(int timeoutMillis) {
			this.readtime = timeoutMillis;
		}

		public void setChunkedStreamingMode(int chunkLength) {
			this.chunkLength = chunkLength;
		}

		private File getFile(String filename) {
			return new File(Text2SpeechService.this.tempRoot, filename);
		}

		public void download(final List<URL> url, final String filename, final Text2SpeechService.OnDownListener listener) {
			this.isstop = false;
			this.downListener = listener;
			Thread thread = new Thread(new Runnable() {
				public void run() {
					Text2SpeechService.DownHandler.DException exception;
					Message msg;
					try {
						DownHandler.this.doDownload(url, filename, listener);
					} catch (SocketTimeoutException var4) {
						exception = DownHandler.this.new DException();
						exception.e = var4;
						exception.info = "OUT_TIME";
						msg = new Message();
						msg.obj = exception;
						msg.what = 1;
						DownHandler.this.handler.sendMessage(msg);
					} catch (NetworkErrorException var5) {
						exception = DownHandler.this.new DException();
						exception.e = var5;
						exception.info = "NETWORK_ERROR";
						msg = new Message();
						msg.obj = exception;
						msg.what = 1;
						DownHandler.this.handler.sendMessage(msg);
					} catch (UnsupportedEncodingException var6) {
						exception = DownHandler.this.new DException();
						exception.e = var6;
						exception.info = "UN_ENCODE";
						msg = new Message();
						msg.obj = exception;
						msg.what = 1;
						DownHandler.this.handler.sendMessage(msg);
					} catch (IOException var7) {
						exception = DownHandler.this.new DException();
						exception.e = var7;
						exception.info = "IO_ERROR";
						msg = new Message();
						msg.obj = exception;
						msg.what = 1;
						DownHandler.this.handler.sendMessage(msg);
					} catch (Exception var8) {
						exception = DownHandler.this.new DException();
						exception.e = var8;
						exception.info = "UNKOWN_ERROR";
						msg = new Message();
						msg.obj = exception;
						msg.what = 1;
						DownHandler.this.handler.sendMessage(msg);
					}

				}
			});
			thread.start();
		}

		private int getLenghts(List<URL> urls) throws IOException {
			int len = 0;

			for(int i = 0; i < urls.size(); ++i) {
				len += this.getLenght((URL)urls.get(i));
			}

			return len;
		}

		private int getLenght(URL url) throws IOException {
			HttpURLConnection localHttpURLConnection = null;
			localHttpURLConnection = (HttpURLConnection)url.openConnection();
			if (this.chunkLength > 0) {
				localHttpURLConnection.setChunkedStreamingMode(this.chunkLength);
			}

			if (this.outtime > 0) {
				localHttpURLConnection.setConnectTimeout(this.outtime);
			}

			if (this.readtime > 0) {
				localHttpURLConnection.setReadTimeout(this.readtime);
			}

			localHttpURLConnection.setRequestMethod("GET");
			localHttpURLConnection.setDoOutput(false);
			localHttpURLConnection.setDoInput(true);
			localHttpURLConnection.connect();
			int lenght = localHttpURLConnection.getContentLength();
			localHttpURLConnection.disconnect();
			return lenght;
		}

		public int getInputStream(int lenght, int readlenght, URL url, BufferedOutputStream bis) throws SocketTimeoutException, NetworkErrorException, UnsupportedEncodingException, IOException, Exception {
			HttpURLConnection localHttpURLConnection = null;
			localHttpURLConnection = (HttpURLConnection)url.openConnection();
			if (this.chunkLength > 0) {
				localHttpURLConnection.setChunkedStreamingMode(this.chunkLength);
			}

			if (this.outtime > 0) {
				localHttpURLConnection.setConnectTimeout(this.outtime);
			}

			if (this.readtime > 0) {
				localHttpURLConnection.setReadTimeout(this.readtime);
			}

			localHttpURLConnection.setRequestMethod("GET");
			localHttpURLConnection.setDoOutput(false);
			localHttpURLConnection.setDoInput(true);
			localHttpURLConnection.connect();
			int code = localHttpURLConnection.getResponseCode();
			if (code != 200) {
				throw new IOException();
			} else {
				int len = localHttpURLConnection.getContentLength();
				InputStream inputStream = localHttpURLConnection.getInputStream();
				boolean var9 = false;

				int re;
				while((re = inputStream.read(this.data)) != -1) {
					readlenght += re;
					bis.write(this.data, 0, re);
					Message msg = new Message();
					Text2SpeechService.DownHandler.Progress progress = new Text2SpeechService.DownHandler.Progress();
					progress.lenght = lenght;
					progress.progressLenght = readlenght;
					msg.obj = progress;
					msg.what = 4;
					this.handler.sendMessage(msg);
					if (this.isstop) {
						break;
					}
				}

				return len;
			}
		}

		private void doDownload(List<URL> urls, String fileName, Text2SpeechService.OnDownListener listener) throws SocketTimeoutException, NetworkErrorException, UnsupportedEncodingException, IOException, Exception {
			this.handler.sendEmptyMessage(0);
			Exception exception = null;
			int lenght = 0;

			try {
				lenght = this.getLenghts(urls);
			} catch (Exception var12) {
				exception = var12;
			}

			File filetemp;
			if (this.is_return_temp) {
				filetemp = this.getFile(fileName);
				if (filetemp.exists() && (filetemp.length() == (long)lenght || lenght == 0 && filetemp.length() != 0L)) {
					Text2SpeechService.DownHandler.Temp tempx = new Text2SpeechService.DownHandler.Temp();
					tempx.count = 1;
					tempx.file = filetemp;
					tempx.index = 1;
					Message msg2 = new Message();
					msg2.obj = tempx;
					msg2.what = 2;
					this.handler.sendMessage(msg2);
					return;
				}
			} else {
				filetemp = this.getFile(fileName);
				if (filetemp.exists()) {
					this.deleteFile(filetemp);
				}
			}

			if (exception != null) {
				throw exception;
			} else {
				filetemp = this.getFile(fileName);
				if (!filetemp.exists()) {
					filetemp.getParentFile().mkdirs();
				}

				FileOutputStream out = new FileOutputStream(filetemp);
				BufferedOutputStream bis = new BufferedOutputStream(out);
				int readlenght = 0;

				for(int i = 0; i < urls.size(); ++i) {
					readlenght += this.getInputStream(lenght, readlenght, (URL)urls.get(i), bis);
				}

				bis.flush();
				bis.close();
				Message msg = new Message();
				Text2SpeechService.DownHandler.Temp temp = new Text2SpeechService.DownHandler.Temp();
				temp.count = 1;
				temp.file = filetemp;
				temp.index = 1;
				msg.obj = temp;
				msg.what = 2;
				this.handler.sendMessage(msg);
			}
		}

		private void deleteFile(File file) {
			if (file.exists()) {
				if (file.isFile()) {
					file.delete();
				} else if (file.isDirectory()) {
					File[] files = file.listFiles();

					for(int i = 0; i < files.length; ++i) {
						this.deleteFile(files[i]);
					}
				}

				file.delete();
			}

		}

		private class DException {
			public Exception e;
			public String info;

			private DException() {
			}
		}

		private class Progress {
			public int lenght;
			public int progressLenght;

			private Progress() {
			}
		}

		private class Temp {
			public File file;
			public int count;
			public int index;

			private Temp() {
			}
		}
	}

	private interface OnDownListener {
		void onLoading();

		void onResult(File var1, int var2, int var3);

		void onProgress(int var1, int var2);

		void onError(Exception var1, String var2);

		void onStop();
	}

	private static enum State {
		init,
		stop,
		downloading,
		complete,
		error;

		private State() {
		}
	}
}
