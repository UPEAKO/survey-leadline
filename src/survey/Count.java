package survey;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * 读取原始数据文件
 * 写入结果文件
 */
public class Count {
    public static void main(String[] args) {
        String num = "";
        BufferedReader buffer = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("请输入初始数据文件名(文件需在jar包同一路径下):");
        try {
            num = buffer.readLine();
            buffer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        String fileName = "./"+num;
        Tool tool = new Tool(fileName);
        tool.doIt();
        System.out.println("平差结果在同一路径下的result.txt文件中");
    }
}
