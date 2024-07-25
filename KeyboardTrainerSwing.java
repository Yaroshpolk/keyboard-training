import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class KeyboardTrainerSwing extends JFrame {

    // Массив текстовых образцов для печати
    private static final String[] TEXT_SAMPLES = {
            "Java это язык программирования.",
            "Клавиатурный тренажер поможет вам улучшить скорость печати.",
            "Практика делает мастера.",
            "Успех приходит к тем, кто трудится.",
            "Репетируйте свои навыки регулярно."
    };

    // Компоненты пользовательского интерфейса
    private JTextArea textArea;       // Область для ввода текста
    private JLabel label;             // Метка для отображения текста образца
    private JLabel timerLabel;        // Метка для отображения оставшегося времени
    private String sampleText;        // Текущий текст образца для печати
    private int currIndex = 0;        // Текущий индекс введенного символа
    private int totalCharactersTyped = 0; // Общее количество введенных символов
    private Timer timer;              // Таймер для отслеживания времени
    private int timeLeft = 60000;     // оставшееся время в миллисекундах (60 секунд)

    public KeyboardTrainerSwing() {
        // Настройки JFrame
        setTitle("Клавиатурный тренажер");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Получаем случайный текст образца и инициализируем метку
        sampleText = getRandomSample();
        label = new JLabel(getFormattedSampleText(), SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.PLAIN, 24));
        label.setForeground(Color.GRAY);
        add(label, BorderLayout.CENTER); // Добавляем метку в центр окна

        // Инициализация текстовой области для ввода
        textArea = new JTextArea();
        textArea.setFont(new Font("Arial", Font.PLAIN, 24));
        textArea.setLineWrap(true); // Перенос строк
        textArea.setWrapStyleWord(true);
        textArea.setFocusable(true);
        add(textArea, BorderLayout.SOUTH); // Добавляем текстовую область в нижнюю часть окна

        // Инициализация метки таймера
        timerLabel = new JLabel("60.0", SwingConstants.RIGHT);
        timerLabel.setFont(new Font("Arial", Font.PLAIN, 24));
        timerLabel.setForeground(Color.BLACK);
        add(timerLabel, BorderLayout.NORTH); // Добавляем метку таймера в верхнюю часть окна

        // Обработчик нажатий клавиш
        textArea.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char typedChar = e.getKeyChar(); // Получаем введенный символ
                // Проверяем, соответствует ли введенный символ текущему символу образца
                if (currIndex < sampleText.length() && typedChar == sampleText.charAt(currIndex)) {
                    label.setText(getFormattedSampleText()); // Обновляем текст образца
                    currIndex++; // Увеличиваем текущий индекс
                    totalCharactersTyped++; // Увеличиваем счетчик введенных символов
                } else {
                    e.consume(); // Если символ неправильный, игнорируем его
                }

                // Проверяем, закончено ли предложение
                if (currIndex == sampleText.length()) {
                    sampleText = getRandomSample(); // Получаем новый текст образца
                    currIndex = 0; // Сбрасываем текущий индекс
                    label.setText(getFormattedSampleText()); // Обновляем текст образца
                    textArea.setText(""); // Очищаем текстовую область
                }
            }

            @Override
            public void keyPressed(KeyEvent e) {
                // Игнорируем нажатия клавиш Backspace и Delete
                if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE || e.getKeyCode() == KeyEvent.VK_DELETE) {
                    e.consume(); // Не выполняем стандартное действие клавиш
                }
            }
        });

        // Запускаем таймер
        startTimer();
    }

    private void startTimer() {
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            private int elapsedMilliseconds = 0; // Пройденное время в миллисекундах

            @Override
            public void run() {
                // Проверяем, не истекло ли время
                if (elapsedMilliseconds < timeLeft) {
                    elapsedMilliseconds += 100; // Увеличиваем пройденное время на 100 миллисекунд
                    int remainingMilliseconds = timeLeft - elapsedMilliseconds; // Вычисляем оставшееся время
                    int seconds = remainingMilliseconds / 1000; // Получаем секунды
                    int milliseconds = remainingMilliseconds % 100; // Получаем миллисекунды

                    // Обновляем метку таймера
                    SwingUtilities.invokeLater(() -> {
                        timerLabel.setText(String.format("%d.%01d", seconds, milliseconds));
                    });
                } else {
                    timer.cancel(); // Останавливаем таймер
                    endGame(); // Завершаем игру
                }
            }
        }, 0, 100); // Обновляем каждую 100 миллисекунд
    }

    private void endGame() {
        // Показываем диалоговое окно с результатами
        JOptionPane.showMessageDialog(this, String.format("Ваш результат за 60 секунд: %d символов", totalCharactersTyped));
        System.exit(0); // Закрываем приложение
    }

    private String getRandomSample() {
        Random random = new Random();
        return TEXT_SAMPLES[random.nextInt(TEXT_SAMPLES.length)]; // Возвращаем случайный текст образца
    }

    private String getFormattedSampleText() {
        StringBuilder formattedText = new StringBuilder("<html>"); // Начинаем формирование HTML текста
        for (int i = 0; i < sampleText.length(); i++) {
            // Форматируем цвет для каждого символа в зависимости от текущего индекса
            if (i < currIndex) {
                formattedText.append("<span style='color: black;'>").append(sampleText.charAt(i)).append("</span>");
            } else if (i == currIndex) {
                formattedText.append("<span style='color: blue;'>").append(sampleText.charAt(i)).append("</span>");
            } else {
                formattedText.append("<span style='color: gray;'>").append(sampleText.charAt(i)).append("</span>");
            }
        }
        formattedText.append("</html>");
        return formattedText.toString(); // Возвращаем форматированный текст
    }

    public static void main(String[] args) {
        // Запускаем приложение в потоке событий Swing
        SwingUtilities.invokeLater(() -> {
            KeyboardTrainerSwing app = new KeyboardTrainerSwing();
            app.setVisible(true); // Делаем приложение видимым
        });
    }
}