package com.aniss.csquiz;

import javax.swing.*;
import javax.sound.sampled.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

public class QuizApplication extends JFrame {

    private Quiz quiz;
    private Clip backgroundMusicClip;
    private boolean musicEnabled = true;
    private boolean isFullscreen = false;
    private Dimension windowedSize = new Dimension(700, 600);
    private Point windowedLocation;

    private JButton musicBtn;
    private JButton fullscreenBtn;
    private JPanel titleBar;

    private final Color ACCENT_CYAN = new Color(0, 240, 255);
    private final Color ERROR = new Color(255, 70, 100);
    private final Color TEXT_SECONDARY = new Color(150, 150, 180);

    public QuizApplication(Quiz quiz) {
        this.quiz = quiz;

        setTitle("CS QUIZ");
        setSize(700, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setUndecorated(true);
        setBackground(new Color(0, 0, 0, 0));

        try {
            Image icon = Toolkit.getDefaultToolkit().getImage("src/icons/CSQuizIcon.png");
            setIconImage(icon);
        } catch (Exception e) {
            System.out.println("Icon not found");
        }

        showMainMenu();
        setVisible(true);

        SwingUtilities.invokeLater(() -> startBackgroundMusic());
    }

    public void showMainMenu() {
        getContentPane().removeAll();

        titleBar = createTitleBar();
        MainMenuPanel menuPanel = new MainMenuPanel(quiz, this);

        JPanel container = new JPanel(new BorderLayout());
        container.add(titleBar, BorderLayout.NORTH);
        container.add(menuPanel, BorderLayout.CENTER);

        add(container);
        revalidate();
        repaint();
    }

    public void showQuiz() {
        getContentPane().removeAll();

        titleBar = createTitleBar();
        QuizPanel quizPanel = new QuizPanel(quiz, this, backgroundMusicClip, musicEnabled);

        JPanel container = new JPanel(new BorderLayout());
        container.add(titleBar, BorderLayout.NORTH);
        container.add(quizPanel, BorderLayout.CENTER);

        add(container);
        revalidate();
        repaint();
    }

    public void showResults() {
        getContentPane().removeAll();

        titleBar = createTitleBar();
        ResultsScreen resultsPanel = new ResultsScreen(quiz, this, backgroundMusicClip, musicEnabled, this);

        JPanel container = new JPanel(new BorderLayout());
        container.add(titleBar, BorderLayout.NORTH);
        container.add(resultsPanel, BorderLayout.CENTER);

        add(container);
        revalidate();
        repaint();
    }

    private JPanel createTitleBar() {
        JPanel titleBar = new JPanel(new BorderLayout());
        titleBar.setBackground(new Color(15, 15, 25));
        titleBar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, ACCENT_CYAN),
                BorderFactory.createEmptyBorder(10, 20, 10, 10)
        ));

        JLabel titleLabel = new JLabel("CS QUIZ");
        titleLabel.setFont(new Font("Consolas", Font.BOLD, 16));
        titleLabel.setForeground(ACCENT_CYAN);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setOpaque(false);

        musicBtn = createTitleBarButton("MUSIC ON");
        musicBtn.addActionListener(e -> {
            toggleBackgroundMusic();
            musicBtn.setText(musicEnabled ? "MUSIC ON" : "MUSIC OFF");
        });

        fullscreenBtn = createTitleBarButton(isFullscreen ? "WINDOWED" : "FULLSCREEN");
        fullscreenBtn.addActionListener(e -> toggleFullscreen());

        JButton minimizeBtn = createTitleBarButton("MIN");
        minimizeBtn.addActionListener(e -> setState(JFrame.ICONIFIED));

        JButton closeBtn = createTitleBarButton("EXIT");
        closeBtn.setForeground(ERROR);
        closeBtn.addActionListener(e -> {
            stopBackgroundMusic();
            dispose();
        });

        buttonPanel.add(musicBtn);
        buttonPanel.add(fullscreenBtn);
        buttonPanel.add(minimizeBtn);
        buttonPanel.add(closeBtn);

        titleBar.add(titleLabel, BorderLayout.WEST);
        titleBar.add(buttonPanel, BorderLayout.EAST);

        MouseAdapter ma = new MouseAdapter() {
            Point initial;
            @Override
            public void mousePressed(MouseEvent e) {
                initial = e.getPoint();
            }
            @Override
            public void mouseDragged(MouseEvent e) {
                if (!isFullscreen) {
                    int x = getLocation().x + e.getX() - initial.x;
                    int y = getLocation().y + e.getY() - initial.y;
                    setLocation(x, y);
                }
            }
        };
        titleBar.addMouseListener(ma);
        titleBar.addMouseMotionListener(ma);

        return titleBar;
    }

    private JButton createTitleBarButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Consolas", Font.BOLD, 11));
        btn.setForeground(TEXT_SECONDARY);
        btn.setBackground(new Color(0, 0, 0, 0));
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setMargin(new Insets(0, 5, 0, 5));

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btn.setForeground(ACCENT_CYAN);
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                if (btn.getText().equals("EXIT")) {
                    btn.setForeground(ERROR);
                } else {
                    btn.setForeground(TEXT_SECONDARY);
                }
            }
        });

        return btn;
    }

    private void toggleFullscreen() {
        if (isFullscreen) {
            setExtendedState(JFrame.NORMAL);
            if (windowedLocation != null) {
                setLocation(windowedLocation);
            }
            setSize(windowedSize);
            fullscreenBtn.setText("FULLSCREEN");
            isFullscreen = false;
        } else {
            windowedLocation = getLocation();
            windowedSize = getSize();
            setExtendedState(JFrame.MAXIMIZED_BOTH);
            fullscreenBtn.setText("WINDOWED");
            isFullscreen = true;
        }
    }

    private void startBackgroundMusic() {
        try {
            File musicFile = new File("src/sounds/background.wav");
            System.out.println("Looking for background music at: " + musicFile.getAbsolutePath());

            if (!musicFile.exists()) {
                System.out.println("Background music file not found!");
                return;
            }

            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(musicFile);
            backgroundMusicClip = AudioSystem.getClip();
            backgroundMusicClip.open(audioInputStream);

            if (backgroundMusicClip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
                FloatControl gainControl = (FloatControl) backgroundMusicClip.getControl(FloatControl.Type.MASTER_GAIN);
                gainControl.setValue(-10.0f);
            }

            backgroundMusicClip.loop(Clip.LOOP_CONTINUOUSLY);
            backgroundMusicClip.start();
            System.out.println("Background music started");
        } catch (Exception e) {
            System.out.println("Error starting background music: " + e.getMessage());
        }
    }

    private void stopBackgroundMusic() {
        if (backgroundMusicClip != null && backgroundMusicClip.isRunning()) {
            backgroundMusicClip.stop();
            backgroundMusicClip.close();
        }
    }

    private void toggleBackgroundMusic() {
        if (backgroundMusicClip == null) return;

        if (musicEnabled) {
            backgroundMusicClip.stop();
            musicEnabled = false;
        } else {
            backgroundMusicClip.start();
            musicEnabled = true;
        }
    }

    public Clip getBackgroundMusicClip() {
        return backgroundMusicClip;
    }

    public boolean isMusicEnabled() {
        return musicEnabled;
    }
}