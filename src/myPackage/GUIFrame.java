package myPackage;

import javax.swing.*;
import java.awt.*;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;

public class GUIFrame extends JFrame {

    private JTextArea chatArea;
    private JTextField inputField;
    private JLabel nicknameLabel;
    private PrintWriter out;
    private String nickname;

    private BlockingQueue<String> queue;   // 메시지 큐 (static 아님)

    public GUIFrame(Socket socket, BlockingQueue<String> queue) throws Exception {

        this.queue = queue;

        // 서버로 메시지 보내기 위한 PrintWriter
        out = new PrintWriter(socket.getOutputStream(), true);

        // 닉네임 입력
        nickname = JOptionPane.showInputDialog(this, "닉네임을 입력하세요:");
        if (nickname == null || nickname.trim().isEmpty()) nickname = "User";

        // 서버에 닉네임 보내기
        out.println("NICK|" + nickname);

        // 채팅창
        chatArea = new JTextArea();
        chatArea.setEditable(true);

        // 입력 필드
        inputField = new JTextField();
        inputField.addActionListener(e -> sendMessage());

        // 상단 패널
        nicknameLabel = new JLabel("Me: " + nickname);
        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(e -> saveChat());

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(nicknameLabel);
        topPanel.add(new JLabel("   "));
        topPanel.add(saveButton);

        // 레이아웃 구성
        setLayout(new BorderLayout());
        add(topPanel, BorderLayout.NORTH);
        add(new JScrollPane(chatArea), BorderLayout.CENTER);
        add(inputField, BorderLayout.SOUTH);

        // 메시지 수신 스레드
        new Thread(() -> {
            while (true) {
                try {
                    String msg = queue.take();   // Receiver가 보낸 메시지를 읽음

                    if (msg.startsWith("JOIN|")) {
                        chatArea.append("*** " + msg.substring(5) + " joined ***\n");
                    }
                    else if (msg.startsWith("LEAVE|")) {
                        chatArea.append("*** " + msg.substring(6) + " left ***\n");
                    }
                    else if (msg.startsWith("MSG|")) {
                        String[] p = msg.split("\\|", 3);
                        chatArea.append("[" + p[1] + "] " + p[2] + "\n");
                    }

                } catch (Exception e) {
                    break;
                }
            }
        }).start();

        setSize(700, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    // 메시지 보내기
    private void sendMessage() {
        String text = inputField.getText().trim();
        if (!text.isEmpty()) {
            out.println("TEXT|" + text);
            inputField.setText("");
        }
    }

    // 채팅 저장
    private void saveChat() {
        try {
            String filename = "chatlog_" + System.currentTimeMillis() + ".txt";
            FileWriter fw = new FileWriter(filename);
            fw.write(chatArea.getText());
            fw.close();

            JOptionPane.showMessageDialog(this, "Saved as " + filename);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}