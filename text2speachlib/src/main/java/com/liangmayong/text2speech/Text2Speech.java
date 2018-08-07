//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.liangmayong.text2speech;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Text2Speech {
	public static final String ACTION_TEXT2SPEECH = "com.liangmayong.text2speech";
	private static boolean isInit = false;
	public static String TEXT2SPEECH_TEMP_DIR = "/text2speech/temp/";
	public static String TEXT2SPEECH_SAVE_DIR = "/text2speech/";
	public static boolean isListener = false;
	private static List<OnText2SpeechListener> listeners = new ArrayList();
	private static OnText2SpeechListener defListener = new OnText2SpeechListener() {
		public void onStart() {
			if (Text2Speech.listeners != null && !Text2Speech.listeners.isEmpty()) {
				for(int i = 0; i < Text2Speech.listeners.size(); ++i) {
					try {
						((OnText2SpeechListener)Text2Speech.listeners.get(i)).onStart();
					} catch (Exception var3) {
						;
					}
				}
			}

			if (Text2Speech.text2SpeechListener != null) {
				Text2Speech.text2SpeechListener.onStart();
			}

			Text2Speech.isListener = true;
		}

		public void onLoadProgress(int progressLenght, int lenght) {
			if (Text2Speech.isListener) {
				if (Text2Speech.listeners != null && !Text2Speech.listeners.isEmpty()) {
					for(int i = 0; i < Text2Speech.listeners.size(); ++i) {
						try {
							((OnText2SpeechListener)Text2Speech.listeners.get(i)).onLoadProgress(progressLenght, lenght);
						} catch (Exception var5) {
							;
						}
					}
				}

				if (Text2Speech.text2SpeechListener != null) {
					Text2Speech.text2SpeechListener.onLoadProgress(progressLenght, lenght);
				}

			}
		}

		public void onError(Exception e, String info) {
			if (Text2Speech.isListener) {
				if (Text2Speech.listeners != null && !Text2Speech.listeners.isEmpty()) {
					for(int i = 0; i < Text2Speech.listeners.size(); ++i) {
						try {
							((OnText2SpeechListener)Text2Speech.listeners.get(i)).onError(e, info);
						} catch (Exception var5) {
							;
						}
					}
				}

				if (Text2Speech.text2SpeechListener != null) {
					Text2Speech.text2SpeechListener.onError(e, info);
				}

				Text2Speech.isListener = false;
			}
		}

		public void onCompletion() {
			if (Text2Speech.isListener) {
				if (Text2Speech.listeners != null && !Text2Speech.listeners.isEmpty()) {
					for(int i = 0; i < Text2Speech.listeners.size(); ++i) {
						try {
							((OnText2SpeechListener)Text2Speech.listeners.get(i)).onCompletion();
						} catch (Exception var3) {
							;
						}
					}
				}

				if (Text2Speech.text2SpeechListener != null) {
					Text2Speech.text2SpeechListener.onCompletion();
				}

				Text2Speech.isListener = false;
			}
		}

		public void onPrepared() {
			if (Text2Speech.isListener) {
				if (Text2Speech.listeners != null && !Text2Speech.listeners.isEmpty()) {
					for(int i = 0; i < Text2Speech.listeners.size(); ++i) {
						try {
							((OnText2SpeechListener)Text2Speech.listeners.get(i)).onPrepared();
						} catch (Exception var3) {
							;
						}
					}
				}

				if (Text2Speech.text2SpeechListener != null) {
					Text2Speech.text2SpeechListener.onPrepared();
				}

			}
		}

		public void onPlayProgress(int currentPosition, int duration) {
			if (Text2Speech.isListener) {
				if (Text2Speech.listeners != null && !Text2Speech.listeners.isEmpty()) {
					for(int i = 0; i < Text2Speech.listeners.size(); ++i) {
						try {
							((OnText2SpeechListener)Text2Speech.listeners.get(i)).onPlayProgress(currentPosition, duration);
						} catch (Exception var5) {
							;
						}
					}
				}

				if (Text2Speech.text2SpeechListener != null) {
					Text2Speech.text2SpeechListener.onPlayProgress(currentPosition, duration);
				}

			}
		}
	};
	private static OnText2SpeechListener text2SpeechListener;

	public Text2Speech() {
	}

	private static void init(Context context) {
		if (!isInit) {
			isInit = true;
			IntentFilter intentFilter = new IntentFilter("com.liangmayong.text2speech");
			Text2Speech.Text2SpeechReceiver receiver = new Text2Speech.Text2SpeechReceiver();
			context.getApplicationContext().registerReceiver(receiver, intentFilter);
		}
	}

	public static boolean isSpeeching() {
		return Text2SpeechService.isSpeeching();
	}

	public static void setOnText2SpeechListener(OnText2SpeechListener text2SpeechListener) {
		Text2Speech.text2SpeechListener = text2SpeechListener;
	}

	public static void addText2SpeechListener(OnText2SpeechListener ttsListener) {
		if (ttsListener != null && !listeners.contains(ttsListener)) {
			listeners.add(ttsListener);
		}

	}

	public static void removeText2SpeechListener(OnText2SpeechListener ttsListener) {
		if (listeners.contains(ttsListener)) {
			listeners.remove(ttsListener);
		}

	}

	public static void speech(Context context, Text2SpeechOptions message) {
		init(context);
		Intent intent = new Intent(context, Text2SpeechService.class);
		intent.putExtra("text", message.getText());
		intent.putExtra("spd", message.getSpd());
		intent.putExtra("readtime", message.getReadtime());
		intent.putExtra("outtime", message.getOuttime());
		intent.putExtra("is_return_temp", message.isReturnTemp());
		intent.putExtra("palyenddelete", message.isEndDelete());
		intent.putExtra("chunkLength", message.getChunkLength());
		context.startService(intent);
	}

	public static void speech(Context context, String msg, int spd, boolean afterDelete) {
		init(context);
		Intent intent = new Intent(context, Text2SpeechService.class);
		intent.putExtra("text", msg);
		intent.putExtra("spd", spd);
		intent.putExtra("palyenddelete", afterDelete);
		context.startService(intent);
	}

	public static void load(Context context, String msg, int spd) {
		init(context);
		Intent intent = new Intent(context, Text2SpeechService.class);
		intent.putExtra("text", msg);
		intent.putExtra("spd", spd);
		intent.putExtra("unplay", 1);
		intent.putExtra("palyenddelete", false);
		context.startService(intent);
	}

	public static void load(Context context, String msg) {
		init(context);
		Intent intent = new Intent(context, Text2SpeechService.class);
		intent.putExtra("text", msg);
		intent.putExtra("unplay", 1);
		intent.putExtra("palyenddelete", false);
		context.startService(intent);
	}

	public static void speech(Context context, String msg, boolean afterDelete) {
		init(context);
		Intent intent = new Intent(context, Text2SpeechService.class);
		intent.putExtra("text", msg);
		intent.putExtra("palyenddelete", afterDelete);
		context.startService(intent);
	}

	public static void shutUp(Context context) {
		init(context);
		Intent intent = new Intent(context, Text2SpeechService.class);
		intent.putExtra("action", "stop");
		context.startService(intent);
	}

	public static void pause(Context context) {
		init(context);
		Intent intent = new Intent(context, Text2SpeechService.class);
		intent.putExtra("action", "paused");
		context.startService(intent);
	}

	public static void replay(Context context) {
		init(context);
		Intent intent = new Intent(context, Text2SpeechService.class);
		intent.putExtra("action", "replay");
		context.startService(intent);
	}

	public static void clearText2SpeechCache(Context context) {
		File temp = new File(context.getExternalCacheDir() + TEXT2SPEECH_TEMP_DIR);
		deleteFile(temp);
		File save = new File(context.getExternalCacheDir() + TEXT2SPEECH_SAVE_DIR);
		deleteFile(save);
	}

	private static void deleteFile(File file) {
		if (file.exists()) {
			if (file.isFile()) {
				file.delete();
			} else if (file.isDirectory()) {
				File[] files = file.listFiles();

				for(int i = 0; i < files.length; ++i) {
					deleteFile(files[i]);
				}
			}

			file.delete();
		}

	}

	private static class Text2SpeechReceiver extends BroadcastReceiver {
		private Text2SpeechReceiver() {
		}

		public void onReceive(Context context, Intent intent) {
			if ("com.liangmayong.text2speech".equals(intent.getAction())) {
				String type = intent.getStringExtra("action_type");
				if (type.equals("error")) {
					Exception exception = null;

					try {
						exception = (Exception)intent.getSerializableExtra("action_exception");
					} catch (Exception var6) {
						;
					}

					String info = intent.getStringExtra("action_info");
					Text2Speech.defListener.onError(exception, info);
				} else if (type.equals("start")) {
					Text2Speech.defListener.onStart();
				} else if (type.equals("completion")) {
					Text2Speech.defListener.onCompletion();
				} else if (type.equals("prepared")) {
					Text2Speech.defListener.onPrepared();
				} else {
					int action_progresslenght;
					int action_lenght;
					if (type.equals("playprogress")) {
						action_progresslenght = intent.getIntExtra("action_currentposition", 0);
						action_lenght = intent.getIntExtra("action_duration", 0);
						Text2Speech.defListener.onPlayProgress(action_progresslenght, action_lenght);
					} else if (type.equals("loadprogress")) {
						action_progresslenght = intent.getIntExtra("action_progresslenght", 0);
						action_lenght = intent.getIntExtra("action_lenght", 0);
						Text2Speech.defListener.onLoadProgress(action_progresslenght, action_lenght);
					}
				}
			}

		}
	}
}
