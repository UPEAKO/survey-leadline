/**
 * 读取原始数据文件
 * 写入结果文件
 */
public class Count {
    public static void main(String[] args) {
        String fileName = "/home/ubd/data/test";
        Tool tool = new Tool(fileName);
        tool.doIt();
    }
}
