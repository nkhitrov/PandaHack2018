import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

public class GUI extends  Thread {
    private ArrayList<SensorGui> cells = new ArrayList<>();
    private JTextArea logger = new JTextArea("Start log:\t\t\t\n");
    private BufferedReader in;

    public GUI(String title, String[] cellNames, String[] sensorsForCell) throws HeadlessException {
        JFrame frame = new JFrame();
        frame.setTitle(title);
        frame.setMinimumSize(new Dimension(600, 400));
        try {
            in = new BufferedReader(new InputStreamReader(new FileInputStream("log.txt")));
        }catch (FileNotFoundException e){
            e.printStackTrace();
        }

        JPanel cellPanel = new JPanel();
        cellPanel.setLayout(new GridBagLayout());

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 1;
        c.weighty = 1;
        c.anchor = GridBagConstraints.WEST;
        c.ipadx = 1;
        c.ipady = 1;
        c.insets = new Insets(10, 8, 20, 8);
        c.fill = GridBagConstraints.HORIZONTAL;

        for (String name: cellNames) {
            SensorGui sensor = new SensorGui(name, sensorsForCell);
            cells.add(sensor);
            cellPanel.add(sensor, c);
            ++c.gridx;
        }
        frame.add(cellPanel);

        JPanel panelLog = new JPanel();
        panelLog.setLayout(new GridBagLayout());

        c.gridx = 0;
        c.weighty = 3;
        c.anchor = GridBagConstraints.EAST;
        c.fill = GridBagConstraints.BOTH;

        logger.setEditable(false);
        JScrollPane jp = new JScrollPane(logger);

        panelLog.add(jp, c);

        frame.add(panelLog, BorderLayout.EAST);

        frame.setPreferredSize(new Dimension(600, 400));
        frame.pack();
        frame.setVisible(true);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }
    //todo поток gui
    public void run(){
        while (true){
            System.out.println("Поток");
            changeAllInfo();
            alarmInfo();
            updateLog();
            try {
                sleep(3500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void changeAllInfo(){
        for(SensorGui sensor : cells){
            for(JTextField text: sensor.texts){
               text.setText(String.valueOf(new Random().nextInt()));
            }
        }
    }

    private void alarmInfo(){

    }

    private void updateLog(){
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        Date date = new Date();

        try {
            String line;
            while ((line = in.readLine()) != null) {
                logger.append(dateFormat.format(date) + " " + line + "\n");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public  class SensorGui extends JPanel{
        private JTextField cellMessage;
        private JLabel cellName;
        private String message = "Статус: ";
        private ArrayList<JLabel> labels;
        private ArrayList<JTextField> texts;

        SensorGui(String cell, String[] sensors){
            this.setLayout(new GridBagLayout());

            cellMessage = new JTextField(message);
            cellName = new JLabel(cell);

            labels = new ArrayList<>();
            texts = new ArrayList<>();

            GridBagConstraints c = new GridBagConstraints();
            c.gridx = 0;
            c.gridy = 0;
            c.weightx = 1;
            c.weighty = 1;
            c.fill = GridBagConstraints.HORIZONTAL;
            cellMessage.setEditable(false);
            this.add(cellMessage, c);
            ++c.gridy;
            this.add(cellName, c);
            ++c.gridy;

            int index = 0;
            for (String name : sensors) {
                labels.add(new JLabel(name));
                texts.add(new JTextField(""));
                texts.get(index).setEditable(false);
                this.add(labels.get(index), c);
                ++c.gridy;
                this.add(texts.get(index), c);
                ++c.gridy;
                ++index;
            }

        }

        public void changeInfo(String data, int indexSensor){
            texts.get(indexSensor).setText(data);
        }

        public void changeMessage(String newMessage){
            cellMessage.setText(message + newMessage);
        }
    }
}


