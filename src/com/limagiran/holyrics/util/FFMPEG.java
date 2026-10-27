package com.limagiran.holyrics.util;

import com.limagiran.holyrics.worker.DownloadWorker;
import java.awt.Dimension;
import java.awt.Window;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.imageio.ImageIO;

public class FFMPEG {
   private static final String URL_DOWNLOAD = "http://www.holyrics.com.br/download/file/ffmpeg.zip";
   private static final File FFMPEG = Values.FILE_FFMPEG;
   private final Map<Process, Boolean> killed = new HashMap<>();
   private final List<Process> process = new ArrayList<>();

   public static boolean checkFile() {
      try {
         return FFMPEG.exists() && FFMPEG.length() == 28577792L;
      } catch (Exception var1) {
         return false;
      }
   }

   public static void download(Window owner, Runnable callback) {
      PopUpWorker.exe(
         new DownloadWorker(owner, "http://www.holyrics.com.br/download/file/ffmpeg.zip", FFMPEG, "a908539a9a6990ada655846ed775dbc57adc413c", true, callback),
         true
      );
   }

   public FFMPEG() {
      Runtime.getRuntime().addShutdownHook(new Thread(this::kill));
   }

   public synchronized void kill() {
      this.process.stream().filter(Process::isAlive).forEach(p -> {
         this.killed.put(p, Boolean.TRUE);
         p.destroy();
      });
   }

   public String info(File video) throws FFMPEG.FFMPEGException {
      try {
         String format = "\"%s\" -i \"%s\" -hide_banner";
         String cmd = String.format(format, FFMPEG.getAbsolutePath(), video.getAbsolutePath());
         ProcessBuilder pb = new ProcessBuilder(cmd.split(" "));
         pb.redirectErrorStream(true);
         return this.getOutput(pb.start());
      } catch (Exception var5) {
         throw new FFMPEG.FFMPEGException("Arquivo ffmpeg não encontrado.", var5);
      }
   }

   public boolean isH264_ACC(File video) throws FFMPEG.FFMPEGException {
      String out = this.info(video);
      return out.contains("Video: h264") && out.contains("Audio: aac");
   }

   public boolean isH264(File video) throws FFMPEG.FFMPEGException {
      try {
         return Arrays.asList(this.info(video).split("\n")).parallelStream().anyMatch(o -> o.contains("Video:") && o.contains("h264"));
      } catch (Exception var3) {
         return false;
      }
   }

   public int duration(File video) throws FFMPEG.FFMPEGException {
      String out = this.info(video);

      try {
         if (out.contains("Duration: ")) {
            out = out.substring(out.indexOf("Duration: ") + 10).trim();
            String[] g = out.substring(0, out.indexOf(".")).split(":");
            return Integer.parseInt(g[0]) * 3600 + Integer.parseInt(g[1]) * 60 + Integer.parseInt(g[2]);
         }
      } catch (Exception var4) {
      }

      throw new FFMPEG.FFMPEGException("Não foi possível identificar a duração do vídeo.\npath: " + video.getAbsolutePath());
   }

   private int durationSafe(File video) {
      try {
         String out = this.info(video);
         if (out.contains("Duration: ")) {
            out = out.substring(out.indexOf("Duration: ") + 10).trim();
            String[] g = out.substring(0, out.indexOf(".")).split(":");
            return Integer.parseInt(g[0]) * 3600 + Integer.parseInt(g[1]) * 60 + Integer.parseInt(g[2]);
         }
      } catch (Exception var4) {
      }

      return -1;
   }

   public int fps(File video) throws FFMPEG.FFMPEGException {
      String out = this.info(video);

      try {
         if (out.contains("fps,")) {
            out = out.substring(0, out.indexOf("fps,")).trim();
            return Integer.parseInt(out.substring(out.lastIndexOf(",") + 1).trim());
         }
      } catch (Exception var4) {
      }

      throw new FFMPEG.FFMPEGException("Não foi possível identificar a quantidade de quadros por segundo do vídeo.\npath: " + video.getAbsolutePath());
   }

   public void makeVideoOrGif(List<BufferedImage> images, File output, int fps, FFMPEG.FFMPEGListener listener) {
      File dir = null;

      try {
         dir = Files.createTempDirectory("makeVideo").toFile();
         dir.deleteOnExit();
         listener.progress("Etapa 1/2: Preparando imagens para criação");

         for (int i = 0; i < images.size(); i++) {
            ImageIO.write(images.get(i), "PNG", new File(dir, i + ".png"));
            listener.progress((double)i / (double)images.size() * 100.0);
         }

         listener.progress(0.0);
         listener.progress("Etapa 2/2: Criando vídeo/gif");
         this.makeVideoOrGif(dir, output, fps, listener);
      } catch (Exception var10) {
         listener.error(var10.getMessage());
      } finally {
         if (dir != null) {
            this.deleteDir(dir);
         }
      }
   }

   public void makeVideoFromPath(File in, File audio, File output, int fps, FFMPEG.FFMPEGListener listener) {
      File dir = null;

      try {
         dir = Files.createTempDirectory("makeVideo").toFile();
         dir.deleteOnExit();
         listener.progress("Etapa 1/2: Preparando imagens para criação");
         File[] files = in.listFiles(f -> {
            String name = f.getName().toLowerCase();
            return name.endsWith(".png") || name.endsWith(".jpg");
         });

         for (int i = 0; i < files.length; i++) {
            files[i].renameTo(new File(dir, i + ".png"));
            listener.progress((double)i / (double)files.length * 100.0);
         }

         listener.progress(0.0);
         listener.progress("Etapa 2/2: Criando vídeo");
         this.makeVideo(dir, audio, output, fps, listener);
      } catch (Exception var12) {
         listener.error(var12.getMessage());
      } finally {
         if (dir != null) {
            this.deleteDir(dir);
         }
      }
   }

   private void makeVideo(File imagePath, File audio, File output, int fps, FFMPEG.FFMPEGListener listener) {
      try {
         String format = "\"%s\" -nostdin -r %s -f image2 -i \"%s\\%%d.png\" -i \"%s\" -vf \"scale=trunc(iw/2)*2:trunc(ih/2)*2\" -c:v libx264 -x264-params cabac=1 -crf 19 -preset slow -movflags +faststart -profile:v baseline -level 3.0 -pix_fmt yuv420p -c:a libvo_aacenc \"%s\" -y";
         Object arg0 = FFMPEG.getAbsolutePath();
         Object arg1 = Integer.toString(fps) + "/1";
         Object arg2 = imagePath.getAbsolutePath();
         Object arg3 = audio.getAbsolutePath();
         Object arg4 = output.getAbsolutePath();
         String cmd = String.format(format, arg0, arg1, arg2, arg3, arg4);
         ProcessBuilder pb = new ProcessBuilder(cmd.split(" "));
         pb.redirectErrorStream(true);
         Process p = pb.start();
         this.progress(p, this.durationSafe(audio), output, listener, 7200);
      } catch (IOException var15) {
         listener.error("Erro ao iniciar o processo.\nVerifique os arquivos e tente novamente.");
      } catch (InterruptedException var16) {
         listener.error("Processo interrompido.");
      } catch (Exception var17) {
         listener.error("Erro ao iniciar o processo.\nVerifique os arquivos e tente novamente.");
      }
   }

   private void makeVideoOrGif(File imagePath, File output, int fps, FFMPEG.FFMPEGListener listener) {
      try {
         String videoCodec = output.getName().endsWith(".mp4")
            ? "-c:v libx264 -x264-params cabac=1 -crf 19 -preset slow -movflags +faststart -profile:v baseline -level 3.0 -pix_fmt yuv420p -c:a libvo_aacenc"
            : "";
         String format = "\"%s\" -nostdin -r %s -f image2 -i \"%s\\%%d.png\" %s \"%s\" -y";
         Object arg0 = FFMPEG.getAbsolutePath();
         Object arg1 = Integer.toString(fps) + "/1";
         Object arg2 = imagePath.getAbsolutePath();
         Object arg4 = output.getAbsolutePath();
         String cmd = String.format(format, arg0, arg1, arg2, videoCodec, arg4);
         ProcessBuilder pb = new ProcessBuilder(cmd.split(" "));
         pb.redirectErrorStream(true);
         Process p = pb.start();
         this.progress(p, imagePath.list().length / fps, output, listener, 240);
      } catch (IOException var15) {
         listener.error("Erro ao iniciar o processo.\nVerifique os arquivos e tente novamente.");
      } catch (InterruptedException var16) {
         listener.error("Processo interrompido.");
      } catch (Exception var17) {
         listener.error("Erro ao iniciar o processo.\nVerifique os arquivos e tente novamente.");
      }
   }

   public void addImagesInVideo(File video, Object[][] map, File output, FFMPEG.FFMPEGListener listener) {
      this.addImagesInVideo(video, null, map, output, listener);
   }

   public void addImagesInVideo(File video, Dimension videoSize, Object[][] map, File output, FFMPEG.FFMPEGListener listener) {
      File tempFile = null;

      try {
         if (videoSize == null) {
            videoSize = new Dimension();
         }

         List<String> list = new ArrayList<>();

         for (Object[] obj : map) {
            list.add(String.format("file '" + ((File)obj[0]).getName() + "'\nduration " + obj[1]));
         }

         tempFile = File.createTempFile("images-list", ".txt");
         tempFile.deleteOnExit();
         RW.write(tempFile, String.join("\n", list), "ISO-8859-1");
         String format = "\"%s\" -nostdin -i \"%s\" -f concat -i \"%s\" -filter_complex \"[0:v]scale=%s:%s[v0];[v0][1]overlay=0:0[v1]\" -map [v1] -c:v libx264 -x264-params cabac=1 -crf 19 -preset slow -movflags +faststart -profile:v baseline -level 3.0 -pix_fmt yuv420p \"%s\" -y";
         Object arg0 = FFMPEG.getAbsolutePath();
         Object arg1 = video.getAbsolutePath();
         Object arg2 = tempFile.toString();
         Object arg3 = videoSize.width;
         Object arg4 = videoSize.height;
         Object arg5 = output.getAbsolutePath();
         String cmd = String.format(format, arg0, arg1, arg2, arg3, arg4, arg5);
         ProcessBuilder pb = new ProcessBuilder(cmd.split(" "));
         pb.directory(((File)map[0][0]).getParentFile());
         pb.redirectErrorStream(true);
         Process p = pb.start();
         this.progress(p, this.durationSafe(video), output, listener, 7200);
      } catch (IOException var23) {
         listener.error("Erro ao iniciar o processo.\nVerifique os arquivos e tente novamente.");
      } catch (InterruptedException var24) {
         listener.error("Processo interrompido.");
      } catch (Exception var25) {
         listener.error("Erro ao iniciar o processo.\nVerifique os arquivos e tente novamente.");
      } finally {
         if (tempFile != null) {
            tempFile.delete();
         }
      }
   }

   public void makeVideo(Object[][] map, File audio, Dimension size, int rate, File output, FFMPEG.FFMPEGListener listener) {
      File tempFile = null;

      try {
         tempFile = File.createTempFile("images-list", ".txt");
         tempFile.deleteOnExit();
         List<String> list = new ArrayList<>();

         for (Object[] obj : map) {
            list.add(String.format("file '" + ((File)obj[0]).getName() + "'\nduration " + obj[1]));
         }

         RW.write(tempFile, String.join("\n", list), "ISO-8859-1");
         String format = "\"%s\" -nostdin -f lavfi -i color=black:duration=14400:size=%sx%s:rate=%s  -i \"%s\"  -f concat -i \"%s\"  -filter_complex \"[0:v:0][2]overlay=0:0[v1]\" -map [v1] -map 1:a:0  -c:a libvo_aacenc -c:v libx264 -x264-params cabac=1 -crf 19 -preset slow -movflags +faststart -profile:v baseline -level 3.0 -pix_fmt yuv420p -shortest \"%s\" -y";
         Object arg0 = FFMPEG.getAbsolutePath();
         Object arg1 = size.width;
         Object arg2 = size.height;
         Object arg3 = rate;
         Object arg4 = audio.toString();
         Object arg5 = tempFile.getAbsolutePath();
         Object arg6 = output.getAbsolutePath();
         String cmd = String.format(format, arg0, arg1, arg2, arg3, arg4, arg5, arg6);
         ProcessBuilder pb = new ProcessBuilder(cmd.split(" "));
         pb.directory(((File)map[0][0]).getParentFile());
         pb.redirectErrorStream(true);
         Process p = pb.start();
         this.progress(p, this.durationSafe(audio), output, listener, 7200);
      } catch (IOException var25) {
         listener.error("Erro ao iniciar o processo.\nVerifique os arquivos e tente novamente.");
      } catch (InterruptedException var26) {
         listener.error("Processo interrompido.");
      } catch (Exception var27) {
         listener.error("Erro ao iniciar o processo.\nVerifique os arquivos e tente novamente.");
      } finally {
         if (tempFile != null) {
            tempFile.delete();
         }
      }
   }

   public void addImagesAndAudioInVideo(File video, Object[][] map, File audio, Dimension videoSize, int duration, File output, FFMPEG.FFMPEGListener listener) {
      File tempFile = null;

      try {
         if (videoSize == null) {
            videoSize = new Dimension();
         }

         List<String> list = new ArrayList<>();

         for (Object[] obj : map) {
            list.add(String.format("file '" + ((File)obj[0]).getName() + "'\nduration " + obj[1]));
         }

         tempFile = File.createTempFile("images-list", ".txt");
         tempFile.deleteOnExit();
         RW.write(tempFile, String.join("\n", list), "ISO-8859-1");
         String format = "\"%s\" -nostdin  -i \"%s\"  -i \"%s\"  -f concat -i \"%s\"  -filter_complex \"[0:v]scale=%s:%s[v0];[v0][2]overlay=0:0[v1]\" -map [v1] -map 1:a:0  -c:a libvo_aacenc -c:v libx264 -x264-params cabac=1 -crf 19 -preset slow -movflags +faststart -profile:v baseline -level 3.0 -pix_fmt yuv420p -shortest \"%s\" -y";
         Object arg0 = FFMPEG.getAbsolutePath();
         Object arg1 = video.toString();
         Object arg2 = audio.toString();
         Object arg3 = tempFile.toString();
         Object arg4 = videoSize.width;
         Object arg5 = videoSize.height;
         Object arg6 = output.getAbsolutePath();
         String cmd = String.format(format, arg0, arg1, arg2, arg3, arg4, arg5, arg6);
         ProcessBuilder pb = new ProcessBuilder(cmd.split(" "));
         pb.directory(((File)map[0][0]).getParentFile());
         pb.redirectErrorStream(true);
         Process p = pb.start();
         this.progress(p, duration, output, listener, 7200);
      } catch (IOException var26) {
         listener.error("Erro ao iniciar o processo.\nVerifique os arquivos e tente novamente.");
      } catch (InterruptedException var27) {
         listener.error("Processo interrompido.");
      } catch (Exception var28) {
         listener.error("Erro ao iniciar o processo.\nVerifique os arquivos e tente novamente.");
      } finally {
         if (tempFile != null) {
            tempFile.delete();
         }
      }
   }

   public void addImagesAndAudioInVideo(File video, Object[][] map, File audio, int duration, File output, FFMPEG.FFMPEGListener listener) {
      this.addImagesAndAudioInVideo(video, map, audio, null, duration, output, listener);
   }

   public void convertToH264(File video, File output, FFMPEG.EncodeAttributes attrib, FFMPEG.FFMPEGListener listener) {
      try {
         attrib = attrib == null ? new FFMPEG.EncodeAttributes(25, null) : attrib;
         String format = "\"%s\" -progress global -nostdin -i \"%s\" %s %s -c:v libx264 -x264-params cabac=1 -crf 19 -preset fast -movflags +faststart -profile:v baseline -level 3.0 -pix_fmt yuv420p -c:a libvo_aacenc \"%s\" -y";
         Object arg0 = FFMPEG.getAbsolutePath();
         Object arg1 = video.getAbsolutePath();
         Object arg2 = attrib.getFpsToString();
         Object arg3 = attrib.getSizeToString();
         Object arg4 = output.getAbsolutePath();
         String cmd = String.format(format, arg0, arg1, arg2, arg3, arg4);
         ProcessBuilder pb = new ProcessBuilder(cmd.split(" "));
         pb.redirectErrorStream(true);
         listener.progress("Convertendo vídeo");
         Process p = pb.start();
         this.progress(p, this.durationSafe(video), output, listener, 300);
      } catch (IOException var14) {
         listener.error("Erro ao iniciar o processo.\nVerifique os arquivos e tente novamente.");
      } catch (InterruptedException var15) {
         listener.error("Processo interrompido.");
      } catch (Exception var16) {
         listener.error("Erro ao iniciar o processo.\nVerifique os arquivos e tente novamente.");
      }
   }

   public void cut(File file, File output, int start, int duration, FFMPEG.FFMPEGListener listener) {
      try {
         String format = "\"%s\" -progress global -nostdin -i \"%s\" -ss %s -codec copy -t %s \"%s\" -y";
         Object arg0 = FFMPEG.getAbsolutePath();
         Object arg1 = file.getAbsolutePath();
         Object arg2 = secondsToHH_MM_SS(start);
         Object arg3 = Integer.toString(duration);
         Object arg4 = output.getAbsolutePath();
         String cmd = String.format(format, arg0, arg1, arg2, arg3, arg4);
         ProcessBuilder pb = new ProcessBuilder(cmd.split(" "));
         pb.redirectErrorStream(true);
         listener.progress("Recortando arquivo");
         Process p = pb.start();
         this.progress(p, this.durationSafe(file), output, listener, 30);
      } catch (IOException var15) {
         listener.error("Erro ao iniciar o processo.\nVerifique os arquivos e tente novamente.");
      } catch (InterruptedException var16) {
         listener.error("Processo interrompido.");
      } catch (Exception var17) {
         listener.error("Erro ao iniciar o processo.\nVerifique os arquivos e tente novamente.");
      }
   }

   public void getMP3(File video, File output, FFMPEG.FFMPEGListener listener) {
      try {
         String format = "\"%s\" -progress global -nostdin -i \"%s\" -vn -ab 256 \"%s\" -y";
         Object arg0 = FFMPEG.getAbsolutePath();
         Object arg1 = video.getAbsolutePath();
         Object arg2 = output.getAbsolutePath();
         String cmd = String.format(format, arg0, arg1, arg2);
         ProcessBuilder pb = new ProcessBuilder(cmd.split(" "));
         pb.redirectErrorStream(true);
         listener.progress("Extraindo áudio do vídeo");
         Process p = pb.start();
         this.progress(p, this.durationSafe(video), output, listener, 30);
      } catch (IOException var11) {
         listener.error("Erro ao iniciar o processo.\nVerifique os arquivos e tente novamente.");
      } catch (InterruptedException var12) {
         listener.error("Processo interrompido.");
      } catch (Exception var13) {
         listener.error("Erro ao iniciar o processo.\nVerifique os arquivos e tente novamente.");
      }
   }

   public void toGIF(File video, File output, Dimension maxSize, int start, int duration, int fps, FFMPEG.FFMPEGListener listener) {
      try {
         maxSize = maxSize == null ? new Dimension(7680, 4320) : maxSize;
         String format = "\"%s\" -progress global -nostdin -i \"%s\" -vf scale=w=%s:h=%s:force_original_aspect_ratio=decrease -ss %s -t %s -r %s \"%s\" -y";
         Object arg0 = FFMPEG.getAbsolutePath();
         Object arg1 = video.getAbsolutePath();
         Object arg2 = maxSize.width;
         Object arg3 = maxSize.height;
         Object arg4 = secondsToHH_MM_SS(start);
         Object arg5 = duration;
         Object arg6 = fps;
         Object arg7 = output.getAbsolutePath();
         String cmd = String.format(format, arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7);
         ProcessBuilder pb = new ProcessBuilder(cmd.split(" "));
         pb.redirectErrorStream(true);
         listener.progress("Convertendo vídeo para gif");
         Process p = pb.start();
         this.progress(p, this.durationSafe(video), output, listener, 24);
      } catch (IOException var20) {
         listener.error("Erro ao iniciar o processo.\nVerifique os arquivos e tente novamente.");
      } catch (InterruptedException var21) {
         listener.error("Processo interrompido.");
      } catch (Exception var22) {
         listener.error("Erro ao iniciar o processo.\nVerifique os arquivos e tente novamente.");
      }
   }

   public void getImage(File video, File output, Dimension maxSize, int point, FFMPEG.FFMPEGListener listener) {
      try {
         maxSize = maxSize == null ? new Dimension(-1, -1) : maxSize;
         String format = "\"%s\" -progress global -nostdin -i \"%s\" -vf scale=w=%s:h=%s:force_original_aspect_ratio=decrease -ss %s -vframes 1 \"%s\" -y";
         Object arg0 = FFMPEG.getAbsolutePath();
         Object arg1 = video.getAbsolutePath();
         Object arg2 = maxSize.width;
         Object arg3 = maxSize.height;
         Object arg4 = secondsToHH_MM_SS(point);
         Object arg5 = output.getAbsolutePath();
         String cmd = String.format(format, arg0, arg1, arg2, arg3, arg4, arg5);
         ProcessBuilder pb = new ProcessBuilder(cmd.split(" "));
         pb.redirectErrorStream(true);
         listener.progress("Extraindo imagem do vídeo");
         Process p = pb.start();
         this.progress(p, 1, output, listener, 10);
      } catch (IOException var16) {
         listener.error("Erro ao iniciar o processo.\nVerifique os arquivos e tente novamente.");
      } catch (InterruptedException var17) {
         listener.error("Processo interrompido.");
      } catch (Exception var18) {
         listener.error("Erro ao iniciar o processo.\nVerifique os arquivos e tente novamente.");
      }
   }

   public void getImages(File video, File output, Dimension maxSize, double fps, FFMPEG.FFMPEGListener listener) {
      try {
         mkdirs(output);
         String format = "\"%s\" -progress global -nostdin -i \"%s\" %s -r %s \"%s\\frame_%%04d.png\" -y";
         Object arg0 = FFMPEG.getAbsolutePath();
         Object arg1 = video.getAbsolutePath();
         Object arg2 = maxSize != null ? String.format("-s %sx%s", maxSize.width, maxSize.height) : "";
         Object arg3 = fps;
         Object arg4 = output.getAbsolutePath();
         String cmd = String.format(format, arg0, arg1, arg2, arg3, arg4);
         ProcessBuilder pb = new ProcessBuilder(cmd.split(" "));
         pb.redirectErrorStream(true);
         listener.progress("Extraindo imagem do vídeo");
         Process p = pb.start();
         this.progress(p, this.durationSafe(video), output, listener, 30);
      } catch (IOException var16) {
         listener.error("Erro ao iniciar o processo.\nVerifique os arquivos e tente novamente.");
      } catch (InterruptedException var17) {
         listener.error("Processo interrompido.");
      } catch (Exception var18) {
         listener.error("Erro ao iniciar o processo.\nVerifique os arquivos e tente novamente.");
      }
   }

   public void resize(File video, File output, Dimension newSize, FFMPEG.FFMPEGListener listener) {
      try {
         String format = "\"%s\" -progress global -nostdin -i \"%s\" -s %sx%s -c:a copy \"%s\" -y";
         Object arg0 = FFMPEG.getAbsolutePath();
         Object arg1 = video.getAbsolutePath();
         Object arg2 = newSize.width;
         Object arg3 = newSize.height;
         Object arg4 = output.getAbsolutePath();
         String cmd = String.format(format, arg0, arg1, arg2, arg3, arg4);
         ProcessBuilder pb = new ProcessBuilder(cmd.split(" "));
         pb.redirectErrorStream(true);
         listener.progress("Redimensionando vídeo");
         Process p = pb.start();
         this.progress(p, this.durationSafe(video), output, listener, 30);
      } catch (IOException var14) {
         listener.error("Erro ao iniciar o processo.\nVerifique os arquivos e tente novamente.");
      } catch (InterruptedException var15) {
         listener.error("Processo interrompido.");
      } catch (Exception var16) {
         listener.error("Erro ao iniciar o processo.\nVerifique os arquivos e tente novamente.");
      }
   }

   public void turnRight(File video, File output, FFMPEG.FFMPEGListener listener) {
      this.turn(video, output, "-metadata:s:v:0 rotate=90", listener);
   }

   public void turnLeft(File video, File output, FFMPEG.FFMPEGListener listener) {
      this.turn(video, output, "-metadata:s:v:0 rotate=270", listener);
   }

   public void turn180(File video, File output, FFMPEG.FFMPEGListener listener) {
      this.turn(video, output, "-metadata:s:v:0 rotate=180", listener);
   }

   private void turn(File video, File output, String turn, FFMPEG.FFMPEGListener listener) {
      try {
         String format = "\"%s\" -progress global -nostdin -i \"%s\" -c copy %s \"%s\" -y";
         Object arg0 = FFMPEG.getAbsolutePath();
         Object arg1 = video.getAbsolutePath();
         Object arg3 = output.getAbsolutePath();
         String cmd = String.format(format, arg0, arg1, turn, arg3);
         ProcessBuilder pb = new ProcessBuilder(cmd.split(" "));
         pb.redirectErrorStream(true);
         listener.progress("Girando vídeo");
         Process p = pb.start();
         this.progress(p, this.durationSafe(video), output, listener, 12);
      } catch (IOException var13) {
         listener.error("Erro ao iniciar o processo.\nVerifique os arquivos e tente novamente.");
      } catch (InterruptedException var14) {
         listener.error("Processo interrompido.");
      } catch (Exception var15) {
         listener.error("Erro ao iniciar o processo.\nVerifique os arquivos e tente novamente.");
      }
   }

   public void concat(List<File> files, File output, FFMPEG.FFMPEGListener listener) {
      try {
         File list = File.createTempFile("list", ".txt");
         list.deleteOnExit();
         RW.write(list, files.stream().map(f -> String.format("%nfile '%s'", f.getAbsolutePath())).reduce("# this is a comment", String::concat), "ISO-8859-1");
         String format = "\"%s\" -nostdin -f concat -i \"%s\" -c copy \"%s\" -y";
         Object arg0 = FFMPEG.getAbsolutePath();
         Object arg1 = list.getAbsolutePath();
         Object arg2 = output.getAbsolutePath();
         String cmd = String.format(format, arg0, arg1, arg2);
         ProcessBuilder pb = new ProcessBuilder(cmd.split(" "));
         pb.redirectErrorStream(true);
         listener.progress("Concatenando Vídeos");
         Process p = pb.start();
         this.progress(p, 1, output, listener, 20);
      } catch (IOException var12) {
         listener.error("Erro ao iniciar o processo.\nVerifique os arquivos e tente novamente.");
      } catch (InterruptedException var13) {
         listener.error("Processo interrompido.");
      } catch (Exception var14) {
         listener.error("Erro ao iniciar o processo.\nVerifique os arquivos e tente novamente.");
      }
   }

   public boolean enlarge(File video, File output, int minDuration, int timeout) {
      Process p = null;

      boolean var15;
      try {
         File list = File.createTempFile("list", ".txt");
         list.deleteOnExit();
         double d = (double)this.duration(video);
         if (d < 1.0 || d >= (double)minDuration) {
            return false;
         }

         RW.write(
            list,
            IntStream.range(0, (int)Math.ceil((double)minDuration / d))
               .mapToObj(i -> String.format("%nfile '%s'", video.getAbsolutePath()))
               .reduce("# this is a comment", String::concat),
            "ISO-8859-1"
         );
         String format = "\"%s\" -nostdin -f concat -i \"%s\" -c copy \"%s\" -y";
         Object arg0 = FFMPEG.getAbsolutePath();
         Object arg1 = list.getAbsolutePath();
         Object arg2 = output.getAbsolutePath();
         String cmd = String.format(format, arg0, arg1, arg2);
         ProcessBuilder pb = new ProcessBuilder(cmd.split(" "));
         pb.redirectErrorStream(true);
         p = pb.start();
         this.process.add(p);
         var15 = p.waitFor((long)timeout, TimeUnit.SECONDS) && p.waitFor() == 0 && output.exists();
      } catch (Exception var26) {
         return false;
      } finally {
         try {
            p.destroy();
         } catch (Exception var25) {
         }
      }

      return var15;
   }

   public boolean enlarge(File video, File output, int minDuration) {
      return this.enlarge(video, output, minDuration, 12);
   }

   private void progress(Process p, int duration, File output, FFMPEG.FFMPEGListener listener, int maxDurationSec) throws InterruptedException {
      this.process.add(p);
      List<String> lines = new ArrayList<>();

      try (
         InputStream is = p.getInputStream();
         InputStreamReader isr = new InputStreamReader(is, "cp850");
         BufferedReader br = new BufferedReader(isr);
      ) {
         Utils.startThreadDaemon(() -> {
            try {
               if (p.waitFor((long)maxDurationSec, TimeUnit.SECONDS) && p.waitFor() == 0) {
                  if (output.exists()) {
                     listener.success();
                  } else {
                     listener.error("(Acesso negado) Não foi possível gravar o arquivo no destino escolhido.");
                  }
               } else if (!this.killed.getOrDefault(p, Boolean.FALSE)) {
                  listener.error(lines.isEmpty() ? "Processo interrompido." : lines.get(lines.size() - 1));
               }
            } catch (Exception var11x) {
            } finally {
               Utils.safeException(() -> p.destroy());
               Utils.safeException2(() -> br.close());
            }
         });

         String s;
         while ((s = br.readLine()) != null) {
            try {
               lines.add(s);
               if (duration > 0 && s.contains("time=")) {
                  s = s.substring(s.indexOf("time=") + 5).trim();
                  String[] g = s.substring(0, s.indexOf(".")).split(":");
                  double time = (double)(Integer.parseInt(g[0]) * 3600 + Integer.parseInt(g[1]) * 60 + Integer.parseInt(g[2]));
                  listener.progress(time / (double)duration * 100.0);
               }
            } catch (Exception var65) {
            }
         }
      } catch (Exception var72) {
      }
   }

   private String getOutput(Process p) {
      this.process.add(p);
      List<String> lines = new ArrayList<>();

      String s;
      try (
         InputStreamReader isr = new InputStreamReader(p.getInputStream(), "cp850");
         BufferedReader stdInput = new BufferedReader(isr);
      ) {
         while ((s = stdInput.readLine()) != null) {
            lines.add(s);
         }
      } catch (Exception var35) {
      }

      return lines.stream().collect(Collectors.joining("\n"));
   }

   private boolean deleteDir(File dir) {
      if (dir.isDirectory()) {
         String[] files = dir.list();

         for (String file : files) {
            boolean success = this.deleteDir(new File(dir, file));
            if (!success) {
               return false;
            }
         }
      }

      return dir.delete();
   }

   private static void mkdirs(File file) {
      try {
         file = file.getAbsoluteFile();
         if (file.getName().contains(".")) {
            file.getParentFile().mkdirs();
         } else {
            file.mkdirs();
         }
      } catch (Exception var2) {
      }
   }

   private static String secondsToHH_MM_SS(int s) {
      return s <= 0 ? "00:00:00" : String.format("%02d:%02d:%02d", s / 3600, s % 3600 / 60, s % 60);
   }

   public static class EncodeAttributes {
      private int fps = 30;
      private Dimension size;

      public EncodeAttributes() {
      }

      public EncodeAttributes(int fps, Dimension size) {
         this.fps = fps;
         this.size = size;
      }

      public int getFps() {
         return this.fps;
      }

      public FFMPEG.EncodeAttributes setFps(int fps) {
         this.fps = fps;
         return this;
      }

      public String getFpsToString() {
         return "-r " + this.fps;
      }

      public Dimension getSize() {
         return this.size;
      }

      public FFMPEG.EncodeAttributes setSize(Dimension size) {
         this.size = size;
         return this;
      }

      public String getSizeToString() {
         return this.size == null ? "" : String.format("-s %sx%s", this.size.width, this.size.height);
      }
   }

   public static class FFMPEGAdapter implements FFMPEG.FFMPEGListener {
      @Override
      public void progress(double value) {
      }

      @Override
      public void progress(String msg) {
      }

      @Override
      public void error(String msg) {
      }

      @Override
      public void success() {
      }
   }

   public static class FFMPEGException extends Exception {
      public FFMPEGException() {
      }

      public FFMPEGException(String message) {
         super(message);
      }

      public FFMPEGException(String message, Throwable cause) {
         super(message, cause);
      }

      public FFMPEGException(Throwable cause) {
         super(cause);
      }

      public FFMPEGException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
         super(message, cause, enableSuppression, writableStackTrace);
      }
   }

   public interface FFMPEGListener {
      void progress(double var1);

      void progress(String var1);

      void error(String var1);

      void success();
   }
}
