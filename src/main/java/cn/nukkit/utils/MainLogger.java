package cn.nukkit.utils;

import cn.nukkit.Nukkit;
import cn.nukkit.command.CommandReader;
import org.fusesource.jansi.AnsiConsole;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * author: MagicDroidX
 * Nukkit
 */
public class MainLogger extends ThreadedLogger {

    protected File logFile;
    protected String logStream = "";
    protected boolean shutdown;
    protected boolean logDebug = false;

    protected static MainLogger logger;

    public MainLogger(String logFile) {
        this(logFile, false);
    }

    public MainLogger(String logFile, Boolean logDebug) {
        AnsiConsole.systemInstall();

        if (logger != null) {
            throw new RuntimeException("MainLogger has been already created");
        }
        logger = this;
        this.logFile = new File(logFile);
        if (!this.logFile.exists()) {
            try {
                this.logFile.createNewFile();
            } catch (IOException e) {
                this.logException(e);
            }
        }
        this.logDebug = logDebug;
        this.start();
    }

    public static MainLogger getLogger() {
        return logger;
    }

    @Override
    public void emergency(String message) {
        this.send(TextFormat.RED + "[EMERGENCY] " + message);
    }

    @Override
    public void alert(String message) {
        this.send(TextFormat.RED + "[ALERT] " + message);
    }

    @Override
    public void critical(String message) {
        this.send(TextFormat.RED + "[CRITICAL] " + message);
    }

    @Override
    public void error(String message) {
        this.send(TextFormat.DARK_RED + "[ERROR] " + message);
    }

    @Override
    public void warning(String message) {
        this.send(TextFormat.YELLOW + "[WARNING] " + message);
    }

    @Override
    public void notice(String message) {
        this.send(TextFormat.AQUA + "[NOTICE] " + message);
    }

    @Override
    public void info(String message) {
        this.send(TextFormat.WHITE + "[INFO] " + message);
    }

    @Override
    public void debug(String message) {
        if (!this.logDebug) {
            return;
        }
        this.send(TextFormat.GRAY + "[DEBUG] " + message);
    }

    public void setLogDebug(Boolean logDebug) {
        this.logDebug = logDebug;
    }

    public void logException(Exception e) {
        this.logException(e, e.getStackTrace());
    }

    public void logException(Exception e, StackTraceElement[] trace) {
        this.alert(Utils.getExceptionMessage(e));
    }

    @Override
    public void log(LogLevel level, String message) {
        switch (level) {
            case EMERGENCY:
                this.emergency(message);
                break;
            case ALERT:
                this.alert(message);
                break;
            case CRITICAL:
                this.critical(message);
                break;
            case ERROR:
                this.error(message);
                break;
            case WARNING:
                this.warning(message);
                break;
            case NOTICE:
                this.notice(message);
                break;
            case INFO:
                this.info(message);
                break;
            case DEBUG:
                this.debug(message);
                break;
        }
    }

    public void shutdown() {
        this.shutdown = true;
    }

    protected void send(String message) {
        this.send(message, -1);
    }

    protected void send(String message, int level) {
        Date now = new Date();
        String cleanMessage = new SimpleDateFormat("HH:mm:ss").format(now) + " " + TextFormat.clean(message);
        message = TextFormat.toANSI(TextFormat.AQUA + new SimpleDateFormat("HH:mm:ss").format(now) + TextFormat.RESET + " " + message + TextFormat.RESET);
        CommandReader.getInstance().stashLine();
        if (Nukkit.ANSI) {
            System.out.println(message);
        } else {
            System.out.println(cleanMessage);
        }
        CommandReader.getInstance().unstashLine();
        String str = new SimpleDateFormat("Y-M-d").format(now) + " " + cleanMessage + "" + "\r\n";
        /*synchronized (this) {
            this.logStream += str;
            this.notify();
        }*/
        this.logStream += str;
    }

    @Override
    public void run() {
        this.shutdown = false;
        while (!this.shutdown) {
            synchronized (this) {
                if (this.logStream.length() > 0) {
                    String chunk = this.logStream;
                    this.logStream = "";
                    try {
                        FileWriter fileWriter = new FileWriter(this.logFile, true);
                        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
                        bufferedWriter.write(chunk);
                        bufferedWriter.close();
                    } catch (IOException e) {
                        this.logException(e);
                    }
                }
                try {
                    wait(25000);
                } catch (InterruptedException e) {
                    //igonre
                }
            }
        }
        if (this.logStream.length() > 0) {
            String chunk = this.logStream;
            this.logStream = "";
            try {
                FileWriter fileWriter = new FileWriter(this.logFile, true);
                BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
                bufferedWriter.write(chunk);
                bufferedWriter.close();
            } catch (IOException e) {
                this.logException(e);
            }
        }
    }

}
