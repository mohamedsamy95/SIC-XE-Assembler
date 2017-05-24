package sample.mainbody;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;

public class ProgramBlocks {

    private String filename;
    private boolean isCode=true;
    private boolean isData=false;;
    private boolean isBlocks=false;
    private FileInputStream fstream=null;
    private ArrayList<String> Code;
    private ArrayList<String> Data;
    private ArrayList<String> Blocks;
    private String END;

    public ProgramBlocks(String Filename)
    {
        Code = new ArrayList<String>();
        Data = new ArrayList<String>();
        Blocks = new ArrayList<String>();
        filename=Filename;
        start();
    }

    private void start()
    {
        try {
            fstream = new FileInputStream(filename);
        } catch (FileNotFoundException e) {
            System.err.println("File not found!");
        }
        BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
        String readLine = null;
        try {
            while ((readLine=br.readLine())!=null)
            {
                String[] Line = readLine.split("[ ]+");
                if (Line[0].equals("END"))
                {
                    END = new String (readLine);
                    break;
                }
                else if (Line.length>1)
                    if(Line[1].equals("END"))
                    {
                        END = new String (readLine);
                        break;
                    }
                //System.out.println(Line[0]);
                if (Line[0].equals("USE"))
                {
                    System.out.println("Here");
                    if(Line.length>1)
                    {
                        if(Line[1].equals("CDATA"))

                        {
                            System.out.println("Here data");
                            isData=true;
                            isCode=isBlocks=false;
                        }
                        else if (Line[1].equals("CBLKS"))
                        {
                            System.out.println("Here blocks");
                            isBlocks=true;
                            isCode=isData=false;
                        }
                    }
                    else
                    {
                        isCode=true;
                        isBlocks=isData=false;
                    }
                    continue;
                }
                else if (Line.length>1)
                    if(Line[1].equals("USE"))
                    {
                        {
                            System.out.println("Here");
                            if(Line.length>2)
                            {
                                if(Line[2].equals("CDATA"))

                                {
                                    System.out.println("Here data");
                                    isData=true;
                                    isCode=isBlocks=false;
                                }
                                else if (Line[2].equals("CBLKS"))
                                {
                                    System.out.println("Here blocks");
                                    isBlocks=true;
                                    isCode=isData=false;
                                }
                            }
                            else
                            {
                                isCode=true;
                                isBlocks=isData=false;
                            }
                            continue;
                        }
                    }

                if(isCode)
                    Code.add(readLine);
                else if(isData)
                    Data.add(readLine);
                else if(isBlocks)
                    Blocks.add(readLine);

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        PrintWriter out = null;
        try {
            out = new PrintWriter(new OutputStreamWriter(
                    new BufferedOutputStream(new FileOutputStream("code.txt")), "UTF-8"));
            for (String code : Code)
            {
                System.out.println(code);
                out.println(code);
            }
            for (String data : Data)
            {
                System.out.println(data);
                out.println(data);
            }
            for (String block : Blocks)
            {
                System.out.println(block);
                out.println(block);
            }
            out.println(END);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if(out != null) {
                out.flush();
                out.close();
            }
        }
    }



}

