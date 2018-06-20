import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * 1.所有角度在计算时转化为秒，输出时格式化
 * 2.所有成员变量均存储角值的秒值
 * 3.初始坐标及固定角单独存储
 * 数据输入格式：
 * 固定角，方位角
 * 初始坐标
 * 角，边
 */
public class Tool {
    private String fileName;
    //初始方位角
    private int direction = 0;
    //固定角
    private int angle0 = 0;
    //初始坐标
    private PXY zeroXY = new PXY(0,0);
    //所测角列表及大小标志
    private List<eachAngleWithSign> angles = new ArrayList<>();
    //所测边列表
    private List<Double> lengths = new ArrayList<>();

    //改正后角值
    private List<Integer> anglesLater = new ArrayList<>();
    //方位角列表
    private List<Integer>  anglesInDirection = new ArrayList<>();
    //改正前坐标增量列表
    private List<PXY> addXY = new ArrayList<>();
    //改正后坐标增量列表
    private List<PXY> addXYLater = new ArrayList<>();
    //最终坐标
    private List<PXY> XY = new ArrayList<>();

    //闭合差
    private int errorByPlusAngle = 0;
    //导线全场闭合差
    private double errorForLength = 0.0;

    public Tool(String fileName) {
        this.fileName = fileName;
    }

    /**
     * 读文件
     * @return
     * 返回每一行数据的字符串列表
     */
    private List<String> readFile() {
        List<String> angle = new ArrayList<>();

        File file = new File(fileName);

        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            String temp;
            while ((temp = bufferedReader.readLine()) != null) {
                angle.add(temp);
            }

            fileInputStream.close();
            inputStreamReader.close();
            bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return angle;
    }

    /**
     * 将字符串列表转化为成员变量中的数据
     * @param strings
     * 字符串列表
     */
    private void getEachData(List<String> strings) {
        int sign = 0;
        for (int i = 0; i < strings.size(); i++) {
            String [] stringsTwoPart = strings.get(i).split(";");
            if (i == 0) {
                String[] angleZero = stringsTwoPart[0].split(",");
                angle0 = Integer.valueOf(angleZero[0])*60*60+Integer.valueOf(angleZero[1])*60+Integer.valueOf(angleZero[2]);

                /*
                **原来直接输入初始方位角
                String[] direct = stringsTwoPart[1].split(",");
                direction = Integer.valueOf(direct[0])*60*60+Integer.valueOf(direct[1])*60+Integer.valueOf(direct[2]);
                */

                //现在输入初始边的两端点坐标反算方位角
                String [] twoCoorForAngle0 = stringsTwoPart[1].split(",");
                //此处方向为1->2
                double x1 = Double.valueOf(twoCoorForAngle0[0]);
                double y1 = Double.valueOf(twoCoorForAngle0[1]);
                double x2 = Double.valueOf(twoCoorForAngle0[2]);
                double y2 = Double.valueOf(twoCoorForAngle0[3]);
                //Math.atan()在正负PI/2之间
                double angle_double = Math.atan((y2 - y1)/(x2 - x1));
                if (angle_double > 0) {
                    if (y2 < y1)
                        angle_double += Math.PI;
                } else {
                    if (y2 > y1)
                        angle_double += Math.PI;
                    else
                        angle_double += 2*Math.PI;
                }
                direction = (int) ((angle_double/Math.PI)*180*60*60 + 0.5);
            } else if (i == 1){
                double x = Double.valueOf(stringsTwoPart[0]);
                double y = Double.valueOf(stringsTwoPart[1]);
                zeroXY.set(x,y);
            } else {
                String[] angle = stringsTwoPart[0].split(",");
                angles.add(new eachAngleWithSign(Integer.valueOf(angle[0])*60*60+Integer.valueOf(angle[1])*60+Integer.valueOf(angle[2]),sign));
                sign++;

                lengths.add(Double.valueOf(stringsTwoPart[1]));
            }
        }
    }

    /**
     * Do it
     */
    public void doIt() {
        getEachData(readFile());
        steps();
        writeResult();
    }

    /**
     * 所有步骤
     */
    private void steps() {
        step1();
        step2();
        step3();
        step4();
        step5();
    }

    /**
     * 改正后角值
     * 误差分配基本思路：
     * 1.对多余的未能整除的误差值最大的那几个数每个角值加减1
     */
    private void step1() {
        int angleAll = 0;
        for (eachAngleWithSign angle : angles) {
            angleAll += angle.angle;
        }
        int errorAngel = angleAll - 180*60*60*(angles.size()-2);
        errorByPlusAngle = errorAngel;

        List<eachAngleWithSign> anglesCopy = new ArrayList<>();
        anglesCopy.addAll(angles);

        //暂时这样
        for (int i = 0; i < anglesCopy.size(); i++) {
            for (int j = 0; j < anglesCopy.size() - 1; j++) {
                eachAngleWithSign temp = new eachAngleWithSign(0, 0);
                if (anglesCopy.get(j).angle < anglesCopy.get(j+1).angle) {
                    temp.set(anglesCopy.get(j));
                    anglesCopy.set(j,anglesCopy.get(j+1));
                    anglesCopy.set(j+1,temp);
                }
            }
        }

        int num = anglesCopy.size();
        int otherDeltaAngle = errorAngel%num;
        for (int i = 0; i < num; i++) {
            if (i < Math.abs(otherDeltaAngle)&&errorAngel>0) {
                anglesCopy.get(i).setAngle(anglesCopy.get(i).angle - errorAngel/num - 1);
            } else if (i < Math.abs(otherDeltaAngle)&&errorAngel<0) {
                anglesCopy.get(i).setAngle(anglesCopy.get(i).angle - errorAngel/num + 1);
            } else {
                anglesCopy.get(i).setAngle(anglesCopy.get(i).angle - errorAngel/num);
            }
        }

        for (int i = 0; i < num; i++) {
            anglesLater.add(i);
        }

        for (int i = 0; i < num; i++) {
            anglesLater.set(anglesCopy.get(i).sign,anglesCopy.get(i).angle);
        }
    }


    /**
     * 坐标方位角
     */
    private void step2() {
        int num = anglesLater.size();
        int currentDirection = direction;
        //设置第一个角度增量
        anglesLater.set(0,anglesLater.get(0)+angle0);

        for (int i = 0; i < num; i++) {
            currentDirection = currentDirection + anglesLater.get(i) - 180*60*60;
            if (currentDirection < 0)
                currentDirection += 360*60*60;
            anglesInDirection.add(currentDirection);
        }
    }

    /**
     * 改正前坐标增量
     */
    private void step3() {
        int num = anglesInDirection.size();
        for (int i = 0; i < num; i++) {
            double sitar = ((double)anglesInDirection.get(i))/(180*60*60d); //此处整数除法，舍去了尾数,进行强制类型转换
            double deltaX = lengths.get(i)*Math.cos(sitar);
            double deltaY = lengths.get(i)*Math.sin(sitar);
            addXY.add(new PXY(deltaX,deltaY));
        }
    }

    /**
     * 改正后坐标增量
     */
    private void step4() {
        double deltaPlusDeltaX = 0;
        double deltaPlusDeltaY = 0;
        double plusLength = 0;
        int num = addXY.size();
        for (int i = 0; i < num; i++) {
            deltaPlusDeltaX += addXY.get(i).getX();
            deltaPlusDeltaY += addXY.get(i).getY();
            plusLength += lengths.get(i);
        }


        errorForLength = Math.sqrt(deltaPlusDeltaX*deltaPlusDeltaX + deltaPlusDeltaY*deltaPlusDeltaY)/plusLength;

        for (int i = 0; i < num; i++) {
            double laterDeltaX = addXY.get(i).getX() - deltaPlusDeltaX*lengths.get(i)/plusLength;
            double laterDeltaY = addXY.get(i).getY() - deltaPlusDeltaY*lengths.get(i)/plusLength;
            addXYLater.add(new PXY(laterDeltaX,laterDeltaY));
        }
    }

    /**
     * 结果坐标
     */
    private void step5() {
        int num = addXYLater.size();
        double currentX = zeroXY.getX();
        double currentY = zeroXY.getY();
        for (int i = 0; i < num; i++) {
            currentX += addXYLater.get(i).getX();
            currentY += addXYLater.get(i).getY();
            XY.add(new PXY(currentX,currentY));
        }
    }

    /**
     * 将结果写入文件
     * 所写入数据均为字符串
     */
    private void writeResult() {
        //将第一个角值改回多边形内角
        anglesLater.set(0,anglesLater.get(0) - angle0);

        int num = anglesLater.size();
        List<String> results = new ArrayList<>();
        for (int i =0; i < num; i++) {
            String sToWrite = changeToFormat(anglesLater.get(i),anglesInDirection.get(i),addXY.get(i),addXYLater.get(i),XY.get(i));
            results.add(sToWrite);
        }

        results.add("闭合差: "+Integer.toString(errorByPlusAngle));
        results.add("导线全长相对闭合差： "+Double.toString(errorForLength));

        //
        try {
            Files.write(Paths.get("/home/ubd/data/result.txt"),results);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 将数据格式化且为字符串
     */
    private String changeToFormat(int angleLater,int angleInDirection,PXY deltaXY,PXY deltaXYLater,PXY xy) {
        //角值度分秒 a1b1c1
        int c1 = angleLater%60;
        int b1 = (angleLater%(60*60) - c1)/60;
        int a1 = (angleLater - b1*60 - c1)/(60*60);
        //方位角度分秒 a2b2c2
        int c2 = angleInDirection%60;//59
        int b2 = (angleInDirection%(60*60) - c2)/60;//15
        int a2 = (angleInDirection - b2*60 - c2)/(60*60);
        //
        String abc1 = String.format("%-15s",Integer.toString(a1)+" "+Integer.toString(b1)+" "+Integer.toString(c1));
        String abc2 = String.format("%-15s",Integer.toString(a2)+" "+Integer.toString(b2)+" "+Integer.toString(c2));
        //
        String s1 = String.format("%-40s",Double.toString(deltaXY.getX())+","+Double.toString(deltaXY.getY()));
        String s2 = String.format("%-40s",Double.toString(deltaXYLater.getX())+","+Double.toString(deltaXYLater.getY()));
        String s3 = String.format("%-40s",Double.toString(xy.getX())+","+Double.toString(xy.getY()));
        //
        return abc1+abc2+s1+s2+s3;
    }
}
