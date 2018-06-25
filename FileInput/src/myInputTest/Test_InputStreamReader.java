package myInputTest;

import java.io.*;

public class Test_InputStreamReader {
    public static void main(String[] args) {
        //test function readLine() in BufferedReader
        /*try {
            FileInputStream fileInputStream = new FileInputStream("./testForInput.txt");
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String str;
            while ((str = bufferedReader.readLine()) != null) {
                System.out.println(str);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }*/

        //test function read() in BufferedReader
        /*try {
            FileInputStream fileInputStream = new FileInputStream("./testForInput.txt");
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            int c;
            while ((c = bufferedReader.read()) != -1) {
                System.out.println((char) c);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }*/
        //test function read() in InputStreamReader
        /*try {
            FileInputStream fileInputStream = new FileInputStream("./testForInput.txt");
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
            int c;
            while ((c = inputStreamReader.read()) != -1) {
                System.out.println((char) c);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }*/
        //test Writer
        try {
            FileInputStream fileInputStream = new FileInputStream("./testForInput.txt");
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
            OutputStream outputStream = new FileOutputStream("./testForOutput.txt");
            //DataOutputStream can't set charsetName
            //OutputStream outputStream1 = new DataOutputStream(outputStream);
            //charsetName utf-8 should be put in "" instead of ''
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream);
            int c;
            while ((c = inputStreamReader.read()) != -1) {
                outputStreamWriter.append((char) c);
            }
            //before finish flush, nothing is written
            outputStreamWriter.flush();
            fileInputStream.close();
            inputStreamReader.close();
            outputStream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
