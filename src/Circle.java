import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Arc2D;
import java.awt.geom.GeneralPath;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.time.LocalTime;
import java.util.ArrayList;

public class Circle extends JFrame {
    private JPanel panel;
    private JButton addButton;
    private JButton showGridButton;
    private JButton changeColorOfLinesButton;
    private JButton saveAsPNGButton;
    private final JButton[] buttons = new JButton[4];
    //==================================================
    private final int defaultWidth;
    private final int defaultHeight;
    private ArrayList<Goal> sections;
    private int sectionCount = 0;
    private final int maxSteps = 15;

    private boolean showGrid = false;
    private Color lineColor = Color.BLACK;

    int diameter;
    int x;
    int y;
    Color[] pastelColors = {
            new Color(255, 209, 220), // Pastel Pink
            new Color(245, 139, 157), // Pastel Red
            new Color(255, 218, 185), // Pastel Orange
            new Color(255, 255, 153), // Pastel Yellow
            new Color(204, 255, 204), // Pastel Light Green
            new Color(153, 255, 153), // Pastel Dark Green
            new Color(175, 238, 238), // Pastel Turquoise
            new Color(173, 216, 230), // Pastel Light Blue
            new Color(135, 206, 235), // Pastel Dark Blue
            new Color(221, 160, 221)  // Pastel Purple
    };
    String fontFilePath = "assets/coolvetica rg.otf";
    Font font;

    {
        try {
            font = Font.createFont(Font.TRUETYPE_FONT, new File(fontFilePath));
        } catch (FontFormatException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Circle() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int screenWidth = (int) screenSize.getWidth();
        int screenHeight = (int) screenSize.getHeight();

        defaultWidth = screenWidth * 3 / 4;
        defaultHeight = screenHeight * 3 / 4;

        setTitle("wheel of goals");
        setSize(defaultWidth, defaultHeight);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        sections = new ArrayList<>();

        panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // tło
                g.setColor(new Color(253, 195, 107)); // fdc36b
                g.fillRect(0, 0, getWidth(), getHeight());

                // rysowanie białego prostokąta pod napisem
                int margin = 40;
                int rectWidth = getWidth() - 2 * margin;
                int rectHeight = defaultHeight - margin;
                g.setColor(Color.WHITE);
                g.fillRect(margin, margin + 30, rectWidth, rectHeight);

                // rysowanie ciemniejszego prostokąta na górze po lewej stronie
                g.setColor(new Color(194, 133, 0)); // c28500
                int topMargin = 40;
                int leftMargin = 0;
                int rectangleWidth = 600;
                int rectangleHeight = 100;
                g.fillRect(leftMargin, topMargin, rectangleWidth, rectangleHeight);

                g.setColor(Color.WHITE);

                font = font.deriveFont(Font.PLAIN, 70);
                g.setFont(font);
                g.drawString("wheel of goals", leftMargin + 40, topMargin + 70);

                diameter = Math.min(getWidth(), getHeight()) * 3 / 4;
                x = getWidth() - diameter - 120;
                y = (getHeight() - diameter) / 2 + 35;

                // rysowanie koła
                g.fillOval(x, y, diameter, diameter);

                // rysowanie sekcji koła
                drawSections(g2d);

                // rysowanie siatki (jeśli jest włączona)
                if (showGrid) {
                    drawGrid(g2d);
                }
            }
        };

        drawButtons();

        setLayout(new BorderLayout());

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(addButton);
        buttonPanel.add(showGridButton);
        buttonPanel.add(changeColorOfLinesButton);
        buttonPanel.add(saveAsPNGButton);
        add(buttonPanel, BorderLayout.SOUTH);

        add(panel, BorderLayout.CENTER);
    }

    private void drawButtons() {
        addButton = new JButton("add goal");
        showGridButton = new JButton("show grid");
        changeColorOfLinesButton = new JButton("change lines color");
        saveAsPNGButton = new JButton("save as PNG");

        buttons[0] = addButton;
        buttons[1] = showGridButton;
        buttons[2] = changeColorOfLinesButton;
        buttons[3] = saveAsPNGButton;

        for (JButton button : buttons) {
            button.setPreferredSize(new Dimension(200, 40));
            button.setBackground(new Color(194, 133, 0));
            button.setForeground(Color.WHITE);
            font = font.deriveFont(Font.PLAIN, 20);
            button.setFont(font);
            button.setFocusPainted(false);
        }

        addButton.addActionListener(e -> {
            if (sectionCount < 10) {
                sectionCount++;
                addSectionToCircle();
                panel.repaint();
            }
        });
        showGridButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (sectionCount == 0) return;
                showGrid = !showGrid;
                panel.repaint();
            }
        });
        changeColorOfLinesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                changeLinesColor();
            }
        });
        saveAsPNGButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveAsPNG();
            }
        });
    }

    private void drawSections(Graphics2D g2d) {
        FontMetrics metrics = g2d.getFontMetrics();

        for (Goal goal : sections) {

            g2d.setColor(goal.getColor());
            Arc2D.Double arc;

            double startAngle = goal.getStartAngle();
            double angle = goal.getAngle();

            if (sectionCount == 1) {
                arc = new Arc2D.Double(x, y, diameter, diameter, goal.getStartAngle(), goal.getAngle(), Arc2D.OPEN);
            } else {
                arc = new Arc2D.Double(x, y, diameter, diameter, goal.getStartAngle(), goal.getAngle(), Arc2D.PIE);
            }

            GeneralPath path = new GeneralPath();
            path.append(arc, true);
            g2d.fill(path);

            int additionalOffset = 50;

            double midAngle = Math.toRadians(startAngle + angle / 2);
            double cosMid = Math.cos(midAngle);
            double sinMid = Math.sin(midAngle);
            double midX = x + (double) diameter / 2 + (double) diameter / 2 * cosMid + 40;
            double midY = y + (double) diameter / 2 + (double) diameter / 2 * sinMid - 40;

            String goalName = goal.getGoalName();
            int textWidth = metrics.stringWidth(goalName);
            int textHeight = metrics.getHeight();

            int textX = (int) (midX - textWidth / 2);
            int textY = (int) (midY + textHeight / 2);

            GoalNamePosition position = getResult(startAngle, textX, additionalOffset, textY);

            font = font.deriveFont(Font.PLAIN, 20);
            g2d.setFont(font);
            g2d.setColor(Color.BLACK);
            g2d.drawString(goalName, position.textX(), position.textY());

            if (sectionCount >= 1) {
                g2d.setStroke(new BasicStroke(3f));
                g2d.setColor(lineColor);
                g2d.draw(path);
            }
        }
    }

    private GoalNamePosition getResult(double startAngle, int textX, int additionalOffset, int textY) {
        if (startAngle >= 315) {
            textX += additionalOffset;
        }
        if (startAngle > 270 && startAngle < 315) {
            textY -= additionalOffset;
            textX += additionalOffset;
        }
        if (startAngle == 270) {
            textX += additionalOffset;
        }
        if (startAngle >= 225 && startAngle < 270) {
            textX -= additionalOffset;
            textY -= additionalOffset;
        }
        if (startAngle > 180 && startAngle < 225) {
            textX -= additionalOffset;
        }
        if (startAngle == 180) {
            textY -= additionalOffset;
        }
        if (startAngle >= 135 && startAngle < 180) {
            textY += additionalOffset;
            textX -= additionalOffset;
        }
        if (startAngle > 90 && startAngle < 135) {
            textY += additionalOffset;
            textX -= additionalOffset;
        }
        if (startAngle == 90) {
            textX -= additionalOffset;
        }
        if (startAngle >= 45 && startAngle < 90) {
            textY += additionalOffset;
            textX += additionalOffset;
        }
        if (startAngle > 0 && startAngle < 45) {
            textY += additionalOffset;
            textX += additionalOffset;
        }
        if (startAngle == 0 && sectionCount != 1) {
            textY += additionalOffset;
            textX += additionalOffset;
        } else if (startAngle == 0) {
            textX -= additionalOffset;
        }
        GoalNamePosition position = new GoalNamePosition(textX, textY);
        return position;
    }

    private void drawGrid(Graphics2D g2d) {
        g2d.setStroke(new BasicStroke(1f));

        int centerX = x + diameter / 2;
        int centerY = y + diameter / 2;
        double radius = diameter / 2.0;

        for (int i = 1; i <= maxSteps; i++) {
            double currentRadius = radius * (maxSteps - i + 1) / maxSteps;
            int currentDiameter = (int) (currentRadius * 2);
            int currentX = centerX - currentDiameter / 2;
            int currentY = centerY - currentDiameter / 2;

            g2d.drawOval(currentX, currentY, currentDiameter, currentDiameter);
        }
    }

    private void addSectionToCircle() {
        sections.clear();

        if (sectionCount < 1) return;

        double startAngle = 0;
        double angle = 360.0 / sectionCount;

        for (int i = 0; i < sectionCount; i++) {
            sections.add(new Goal(startAngle, angle, pastelColors[i]));
            startAngle += angle;
        }
    }

    private void saveAsPNG() {
        BufferedImage image = new BufferedImage(panel.getWidth(), panel.getHeight(), BufferedImage.TYPE_INT_ARGB);

        Graphics2D g2d = image.createGraphics();
        panel.paint(g2d);
        g2d.dispose();

        String userHome = System.getProperty("user.home");
        String downloadsPath = userHome + File.separator + "Downloads" + File.separator;
        String currentTime = LocalTime.now().toString().substring(0, 5).replace(':', '-');
        String fileNameTmp = "wheel_" + currentTime;
        String fileName = "wheel_" + currentTime + ".png";
        String filePath = downloadsPath + fileName;

        File file = new File(filePath);
        int count = 1;

        while (file.exists()) {
            filePath = downloadsPath + fileNameTmp + "(" + count + ").png";
            file = new File(filePath);
            count++;
        }

        try {
            file = new File(filePath);
            ImageIO.write(image, "png", file);
            System.out.println("Obraz został zapisany jako " + file.getAbsolutePath());

            JOptionPane.showMessageDialog(null, "Plik został pobrany:\n" + filePath, "Pobrano plik", JOptionPane.INFORMATION_MESSAGE);

        } catch (IOException ex) {
            System.err.println("Błąd podczas zapisywania obrazu: " + ex.getMessage());
        }
    }

    private void changeLinesColor() {
        if (lineColor.equals(Color.WHITE)) {
            lineColor = Color.BLACK;
            panel.repaint();
            return;
        }
        lineColor = Color.WHITE;
        panel.repaint();
    }

    private record GoalNamePosition(int textX, int textY) {
    }
}