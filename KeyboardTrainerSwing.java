import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class KeyboardTrainerSwing extends JFrame {

    private static final String[] TEXT_SAMPLES = {
            "Java это язык программирования",
            "Клавиатурный тренажер поможет вам улучшить скорость печати",
            "Практика делает мастера",
            "Успех приходит к тем, кто трудится",
            "Репетируйте свои навыки регулярно"
    };

    private JTextArea inputTextArea;
    private JLabel sampleTextLabel;
    private JLabel timerLabel;
    private String sampleText;
    private int currentIndex = 0;
    private int totalCharactersTyped = 0;
    private Timer timer;
    private int timeLeft = 60000; // Время в миллисекундах (60 секунд)

    public KeyboardTrainerSwing() {
        setTitle("Клавиатурный тренажер");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        sampleText = getRandomSample();
        sampleTextLabel = new JLabel(getFormattedSampleText(), SwingConstants.CENTER);
        sampleTextLabel.setFont(new Font("Arial", Font.PLAIN, 24));
        sampleTextLabel.setForeground(Color.GRAY);
        add(sampleTextLabel, BorderLayout.CENTER);

        inputTextArea = new JTextArea();
        inputTextArea.setFont(new Font("Arial", Font.PLAIN, 24));
        inputTextArea.setLineWrap(true);
        inputTextArea.setWrapStyleWord(true);
        inputTextArea.setFocusable(true);
        add(inputTextArea, BorderLayout.SOUTH);

        timerLabel = new JLabel("60.0", SwingConstants.RIGHT);
        timerLabel.setFont(new Font("Arial", Font.PLAIN, 24));
        timerLabel.setForeground(Color.BLACK);
        add(timerLabel, BorderLayout.NORTH);

        // Обработчик нажатий клавиш
        inputTextArea.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char typedChar = e.getKeyChar();
                if (currentIndex < sampleText.length() && typedChar == sampleText.charAt(currentIndex)) {
                    sampleTextLabel.setText(getFormattedSampleText());
                    currentIndex++;
                    totalCharactersTyped++;
                } else {
                    e.consume();
                }

                // Проверяем, заполнено ли предложение
                if (currentIndex == sampleText.length()) {
                    sampleText = getRandomSample();
                    currentIndex = 0;
                    sampleTextLabel.setText(getFormattedSampleText());
                    inputTextArea.setText("");
                }
            }

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE || e.getKeyCode() == KeyEvent.VK_DELETE) {
                    e.consume();
                }
            }
        });

        startTimer();
    }

    private void startTimer() {
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            private int elapsedMilliseconds = 0;

            @Override
            public void run() {
                if (elapsedMilliseconds < timeLeft) {
                    elapsedMilliseconds += 100; // Увеличиваем время на 100 миллисекунд
                    int remainingMilliseconds = timeLeft - elapsedMilliseconds;
                    int seconds = remainingMilliseconds / 1000;
                    int milliseconds = remainingMilliseconds % 100;

                    SwingUtilities.invokeLater(() -> {
                        timerLabel.setText(String.format("%d.%01d", seconds, milliseconds));
                    });
                } else {
                    timer.cancel();
                    endGame();
                }
            }
        }, 0, 100); // Обновляем каждую 100 миллисекунд
    }

    private void endGame() {
        JOptionPane.showMessageDialog(this, String.format("Ваш результат за 60 секунд: %d символов", totalCharactersTyped));
        System.exit(0);
    }

    private String getRandomSample() {
        Random random = new Random();
        return TEXT_SAMPLES[random.nextInt(TEXT_SAMPLES.length)];
    }

    private String getFormattedSampleText() {
        StringBuilder formattedText = new StringBuilder("<html>");
        for (int i = 0; i < sampleText.length(); i++) {
            if (i < currentIndex) {
                formattedText.append("<span style='color: black;'>").append(sampleText.charAt(i)).append("</span>");
            } else if (i == currentIndex) {
                formattedText.append("<span style='color: blue;'>").append(sampleText.charAt(i)).append("</span>");
            } else {
                formattedText.append("<span style='color: gray;'>").append(sampleText.charAt(i)).append("</span>");
            }
        }
        formattedText.append("</html>");
        return formattedText.toString();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            KeyboardTrainerSwing app = new KeyboardTrainerSwing();
            app.setVisible(true);
        });
    }
}