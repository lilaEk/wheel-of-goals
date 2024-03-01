import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Arc2D;
import java.awt.geom.GeneralPath;
import java.util.ArrayList;

public class Circle extends JFrame {
    private JPanel panel;
    private JButton addButton;
    private JButton toggleGridButton;
    private final int defaultWidth;
    private final int defaultHeight;
//    private ArrayList<Arc2D.Double> sections;
    private ArrayList<Goal> sections;
    private int sectionCount = 0;
    private boolean showGrid = false;

    int diameter;
    int x;
    int y;
    Color[] pastelColors = {
            new Color(255, 209, 220), // Pastel Pink
            new Color(255, 192, 203), // Pastel Red
            new Color(255, 218, 185), // Pastel Orange
            new Color(255, 255, 153), // Pastel Yellow
            new Color(204, 255, 204), // Pastel Light Green
            new Color(153, 255, 153), // Pastel Dark Green
            new Color(175, 238, 238), // Pastel Turquoise
            new Color(173, 216, 230), // Pastel Light Blue
            new Color(135, 206, 235), // Pastel Dark Blue
            new Color(221, 160, 221)  // Pastel Purple
    };

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
                g.fillRect(margin, margin + 50, rectWidth, rectHeight);

                // rysowanie ciemniejszego prostokąta na górze po lewej stronie
                g.setColor(new Color(194, 133, 0)); // c28500
                int topMargin = 40;
                int leftMargin = 0;
                int rectangleWidth = 600;
                int rectangleHeight = 100;
                g.fillRect(leftMargin, topMargin, rectangleWidth, rectangleHeight);

                Font font = new Font("Arial", Font.BOLD, 50);
                g.setFont(font);
                g.setColor(Color.WHITE);
                g.drawString("WHEEL OF GOALS", leftMargin + 20, topMargin + 70);

                diameter = Math.min(getWidth(), getHeight()) * 3 / 4;
                x = getWidth() - diameter - 120;
                y = (getHeight() - diameter) / 2 + 35;

                // rysowanie koła
                g.setColor(new Color(255, 182, 193)); // różowy kolor
                g.fillOval(x, y, diameter, diameter);

                g.setColor(Color.WHITE);
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
        buttonPanel.add(toggleGridButton);
        add(buttonPanel, BorderLayout.SOUTH);

        add(panel, BorderLayout.CENTER);
    }

    private void drawButtons() {
        addButton = new JButton("add goal");
        addButton.setPreferredSize(new Dimension(200, 40));
        addButton.setBackground(new Color(194, 133, 0));
        addButton.setForeground(Color.WHITE);
        addButton.setFont(new Font("Arial", Font.PLAIN, 20));
        addButton.setFocusPainted(false);

        toggleGridButton = new JButton("show grid");
        toggleGridButton.setPreferredSize(new Dimension(200, 40));
        toggleGridButton.setBackground(new Color(194, 133, 0));
        toggleGridButton.setForeground(Color.WHITE);
        toggleGridButton.setFont(new Font("Arial", Font.PLAIN, 20));
        toggleGridButton.setFocusPainted(false);
        toggleGridButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (sectionCount==0) return;
                showGrid = !showGrid;
                panel.repaint();
            }
        });

        addButton.addActionListener(e -> {
            if (sectionCount < 10) {
                sectionCount++;
                addSectionToCircle();
                panel.repaint();
            }
        });
    }

    private void drawSections(Graphics2D g2d) {
        for (Goal goal : sections) {
            g2d.setColor(goal.getColor());
            Arc2D.Double arc = new Arc2D.Double(x, y, diameter, diameter, goal.getStartAngle(), goal.getAngle(), Arc2D.PIE);
            GeneralPath path = new GeneralPath();
            path.append(arc, true);
            g2d.fill(path);
            g2d.setStroke(new BasicStroke(4f));
            g2d.setColor(Color.WHITE);
            g2d.draw(path);
        }
    }

    private void drawGrid(Graphics2D g2d) {
        g2d.setStroke(new BasicStroke(1f));

        int centerX = x + diameter / 2;
        int centerY = y + diameter / 2;
        double radius = diameter / 2.0;

        for (int i = 1; i <= 20; i++) {
            double currentRadius = radius * (20 - i + 1) / 20;
            int currentDiameter = (int) (currentRadius * 2);
            int currentX = centerX - currentDiameter / 2;
            int currentY = centerY - currentDiameter / 2;

            g2d.drawOval(currentX, currentY, currentDiameter, currentDiameter);
        }
    }

    private void addSectionToCircle() {
        sections.clear();

        if (sectionCount <= 1) return;

        double startAngle = 0;
        double angle = 360.0 / sectionCount;

        for (int i = 0; i < sectionCount; i++) {
            sections.add(new Goal(startAngle, angle, pastelColors[i]));
            startAngle += angle;
        }
    }
}