package top.meethigher.update.java.date;

import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 主要
 *
 * @author chenchuancheng github.com/meethigher
 * @date 2023/08/12 09:58
 */
public class Main {

    private static final String dir = "C:\\Users\\meethigher\\Desktop\\route-forward";

    //匹配yyyy/MM/dd HH:mm,其中M和d和H和m存在个位数的情况
    private static final Pattern pattern = Pattern.compile("\\d{4}/\\d{1,2}/\\d{1,2} \\d{1,2}:\\d{1,2}");


    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm");


    public static void main(String[] args) throws Exception {
        List<File> files = walkDirTree(dir, ".java");
        for (File file : files) {
            replace(file);
        }
    }


    public static synchronized void replace(File file) throws Exception {
        BufferedReader reader = new BufferedReader(new FileReader(file));
        StringBuilder sb = new StringBuilder();
        String temp;
        while ((temp = reader.readLine()) != null) {
            sb.append(temp).append(System.lineSeparator());
        }
        reader.close();
        String input = sb.toString();
        Matcher matcher = pattern.matcher(input);
        boolean find = false;
        String source = null;
        while (matcher.find() && !find) {
            find = true;
            source = matcher.group();
        }
        if (!find) {
            return;
        }
//        System.out.println(source);
        //开始判断
        String target = judgeTarget(source);
        if (source.equals(target)) {
            return;
        }
        String result = input.replace(source, target);

        //替换并覆盖原文件
        BufferedWriter writer = new BufferedWriter(new FileWriter(file));
        writer.write(result);
        writer.close();
        System.out.printf("替换 %s -> %s %n", source, target);
    }

    /**
     * 判断目标
     *
     * @param source 源
     * @return {@link String}
     * @throws Exception 异常
     */
    public static synchronized String judgeTarget(String source) throws Exception {
        //开始判断
        Date date = sdf.parse(source);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int i = calendar.get(Calendar.HOUR_OF_DAY);
        if (i >= 9 && i <= 19) {
            calendar.set(Calendar.HOUR_OF_DAY, ThreadLocalRandom.current().nextInt(19, 24));
            return sdf.format(calendar.getTime());
        } else {
            return sdf.format(date);
        }
    }


    /**
     * 走dir
     *
     * @param root      根
     * @param extension 扩展
     * @return {@link List}<{@link File}>
     * @throws Exception 异常
     */
    public static List<File> walkDir(String root, String extension) throws Exception {
        List<File> list = new LinkedList<>();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(root), extension)) {
            for (Path path : stream) {
                System.out.println(path.getFileName());
            }
        }
        return list;
    }

    /**
     * 走dir树
     *
     * @param root      根
     * @param extension 扩展
     * @return {@link List}<{@link File}>
     * @throws Exception 异常
     */
    public static List<File> walkDirTree(String root, String extension) throws Exception {
        List<File> list = new ArrayList<>();
        //遍历文件，访问者模式
        Files.walkFileTree(Paths.get(root), new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                if (file.getFileName().toString().endsWith(extension)) {
                    list.add(file.toFile());
                }
                return super.visitFile(file, attrs);
            }
        });
        return list;

    }

}
