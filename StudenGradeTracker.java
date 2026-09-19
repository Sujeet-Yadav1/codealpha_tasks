import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;

public class StudentGradeTracker extends JFrame {

    // =========================
    // COLORS
    // =========================
    static final Color DARK = new Color(30, 41, 59);
    static final Color BLUE = new Color(37, 99, 235);
    static final Color PURPLE = new Color(124, 58, 237);
    static final Color GREEN = new Color(22, 163, 74);
    static final Color RED = new Color(220, 38, 38);
    static final Color ORANGE = new Color(234, 88, 12);
    static final Color LIGHT = new Color(248, 250, 252);
    static final Color WHITE = Color.WHITE;
    static final Color GRAY = new Color(100, 116, 139);

    // =========================
    // DATA
    // =========================
    static class Student {
        String name;
        int[] marks;

        Student(String name, int[] marks) {
            this.name = name;
            this.marks = marks;
        }

        double getAverage() {
            int sum = 0;

            for (int mark : marks) {
                sum += mark;
            }

            return (double) sum / marks.length;
        }

        int getHighest() {
            int highest = marks[0];

            for (int mark : marks) {
                highest = Math.max(highest, mark);
            }

            return highest;
        }

        int getLowest() {
            int lowest = marks[0];

            for (int mark : marks) {
                lowest = Math.min(lowest, mark);
            }

            return lowest;
        }

        String getGrade() {
            double avg = getAverage();

            if (avg >= 90) return "A+";
            if (avg >= 80) return "A";
            if (avg >= 70) return "B";
            if (avg >= 60) return "C";
            if (avg >= 50) return "D";

            return "F";
        }
    }

    // =========================
    // COMPONENTS
    // =========================

    List<Student> students = new ArrayList<>();

    JTextField nameField;
    JTextField[] markFields = new JTextField[5];

    DefaultTableModel tableModel;
    JTable studentTable;

    JLabel totalStudentsLabel;
    JLabel classAverageLabel;
    JLabel highestMarkLabel;
    JLabel topStudentLabel;

    // =========================
    // CONSTRUCTOR
    // =========================

    public StudentGradeTracker() {

        setTitle("Student Grade Tracker");
        setSize(1100, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        getContentPane().setBackground(LIGHT);

        createUI();
    }

    // =========================
    // MAIN UI
    // =========================

    private void createUI() {

        setLayout(new BorderLayout());

        // -------------------------
        // HEADER
        // -------------------------

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(DARK);
        header.setBorder(new EmptyBorder(20, 30, 20, 30));

        JLabel title = new JLabel("🎓  Student Grade Tracker");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));

        JLabel subtitle = new JLabel(
                "Manage students • Analyze performance • Track grades"
        );

        subtitle.setForeground(new Color(203, 213, 225));
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        JPanel heading = new JPanel();
        heading.setOpaque(false);
        heading.setLayout(new BoxLayout(heading, BoxLayout.Y_AXIS));

        heading.add(title);
        heading.add(Box.createVerticalStrut(5));
        heading.add(subtitle);

        header.add(heading, BorderLayout.WEST);

        add(header, BorderLayout.NORTH);

        // -------------------------
        // LEFT FORM PANEL
        // -------------------------

        JPanel formPanel = new JPanel();
        formPanel.setBackground(WHITE);
        formPanel.setBorder(
                new CompoundBorder(
                        new EmptyBorder(20, 20, 20, 10),
                        new LineBorder(new Color(226, 232, 240), 1, true)
                )
        );

        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));

        JLabel addTitle = new JLabel("Add Student");
        addTitle.setFont(new Font("Segoe UI", Font.BOLD, 21));
        addTitle.setForeground(DARK);

        formPanel.add(addTitle);
        formPanel.add(Box.createVerticalStrut(20));

        // Student Name
        JLabel nameLabel = new JLabel("Student Name");
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));

        nameField = new JTextField();
        styleTextField(nameField);

        formPanel.add(nameLabel);
        formPanel.add(Box.createVerticalStrut(7));
        formPanel.add(nameField);

        formPanel.add(Box.createVerticalStrut(18));

        // Marks
        JLabel marksTitle = new JLabel("Subject Marks");
        marksTitle.setFont(new Font("Segoe UI", Font.BOLD, 15));
        marksTitle.setForeground(DARK);

        formPanel.add(marksTitle);
        formPanel.add(Box.createVerticalStrut(10));

        for (int i = 0; i < 5; i++) {

            JLabel subjectLabel = new JLabel("Subject " + (i + 1));

            subjectLabel.setFont(
                    new Font("Segoe UI", Font.PLAIN, 13)
            );

            markFields[i] = new JTextField();
            styleTextField(markFields[i]);

            JPanel row = new JPanel(new BorderLayout(10, 0));
            row.setBackground(WHITE);

            row.add(subjectLabel, BorderLayout.WEST);
            row.add(markFields[i], BorderLayout.CENTER);

            formPanel.add(row);
            formPanel.add(Box.createVerticalStrut(8));
        }

        formPanel.add(Box.createVerticalStrut(15));

        // Add button
        JButton addButton = createButton(
                "➕  Add Student",
                BLUE
        );

        addButton.addActionListener(e -> addStudent());

        formPanel.add(addButton);

        formPanel.add(Box.createVerticalStrut(10));

        // Clear button
        JButton clearButton = createButton(
                "↻  Clear Form",
                GRAY
        );

        clearButton.addActionListener(e -> clearForm());

        formPanel.add(clearButton);

        // -------------------------
        // CENTER
        // -------------------------

        JPanel centerPanel = new JPanel(new BorderLayout(0, 15));
        centerPanel.setBackground(LIGHT);
        centerPanel.setBorder(new EmptyBorder(20, 10, 20, 20));

        // Statistics cards
        JPanel statsPanel = new JPanel(new GridLayout(1, 4, 12, 0));
        statsPanel.setBackground(LIGHT);

        totalStudentsLabel =
                createStatCard(statsPanel, "👨‍🎓",
                        "Total Students", "0", BLUE);

        classAverageLabel =
                createStatCard(statsPanel, "📊",
                        "Class Average", "0.00", PURPLE);

        highestMarkLabel =
                createStatCard(statsPanel, "🏆",
                        "Highest Mark", "0", ORANGE);

        topStudentLabel =
                createStatCard(statsPanel, "⭐",
                        "Top Student", "-", GREEN);

        centerPanel.add(statsPanel, BorderLayout.NORTH);

        // -------------------------
        // TABLE
        // -------------------------

        String[] columns = {
                "Student",
                "Average",
                "Highest",
                "Lowest",
                "Grade",
                "Performance"
        };

        tableModel = new DefaultTableModel(columns, 0) {

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        studentTable = new JTable(tableModel);

        studentTable.setRowHeight(42);
        studentTable.setFont(
                new Font("Segoe UI", Font.PLAIN, 14)
        );

        studentTable.getTableHeader().setFont(
                new Font("Segoe UI", Font.BOLD, 13)
        );

        studentTable.getTableHeader().setBackground(DARK);
        studentTable.getTableHeader().setForeground(Color.WHITE);

        studentTable.setGridColor(
                new Color(226, 232, 240)
        );

        studentTable.setSelectionBackground(
                new Color(219, 234, 254)
        );

        studentTable.setSelectionForeground(DARK);

        // Grade renderer
        studentTable.getColumnModel()
                .getColumn(4)
                .setCellRenderer(new GradeRenderer());

        JScrollPane scrollPane = new JScrollPane(studentTable);

        scrollPane.setBorder(
                new CompoundBorder(
                        new LineBorder(
                                new Color(226, 232, 240),
                                1,
                                true
                        ),
                        new EmptyBorder(5, 5, 5, 5)
                )
        );

        centerPanel.add(scrollPane, BorderLayout.CENTER);

        // -------------------------
        // ADD PANELS
        // -------------------------

        JPanel leftWrapper = new JPanel(new BorderLayout());
        leftWrapper.setBackground(LIGHT);
        leftWrapper.setBorder(
                new EmptyBorder(20, 20, 20, 10)
        );

        leftWrapper.add(formPanel, BorderLayout.CENTER);

        add(leftWrapper, BorderLayout.WEST);
        add(centerPanel, BorderLayout.CENTER);
    }

    // =========================
    // ADD STUDENT
    // =========================

    private void addStudent() {

        String name = nameField.getText().trim();

        if (name.isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please enter the student's name.",
                    "Input Error",
                    JOptionPane.ERROR_MESSAGE
            );

            return;
        }

        int[] marks = new int[5];

        for (int i = 0; i < 5; i++) {

            String text = markFields[i].getText().trim();

            if (text.isEmpty()) {

                JOptionPane.showMessageDialog(
                        this,
                        "Please enter marks for Subject " + (i + 1),
                        "Input Error",
                        JOptionPane.ERROR_MESSAGE
                );

                return;
            }

            try {

                marks[i] = Integer.parseInt(text);

                if (marks[i] < 0 || marks[i] > 100) {

                    JOptionPane.showMessageDialog(
                            this,
                            "Marks must be between 0 and 100.",
                            "Invalid Marks",
                            JOptionPane.ERROR_MESSAGE
                    );

                    return;
                }

            } catch (NumberFormatException e) {

                JOptionPane.showMessageDialog(
                        this,
                        "Please enter valid numbers for marks.",
                        "Invalid Input",
                        JOptionPane.ERROR_MESSAGE
                );

                return;
            }
        }

        Student student = new Student(name, marks);

        students.add(student);

        updateTable();

        clearForm();

        JOptionPane.showMessageDialog(
                this,
                "Student added successfully! 🎉",
                "Success",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    // =========================
    // UPDATE TABLE
    // =========================

    private void updateTable() {

        tableModel.setRowCount(0);

        for (Student student : students) {

            double average = student.getAverage();

            String performance;

            if (average >= 90)
                performance = "Excellent";
            else if (average >= 80)
                performance = "Very Good";
            else if (average >= 70)
                performance = "Good";
            else if (average >= 60)
                performance = "Average";
            else if (average >= 50)
                performance = "Needs Improvement";
            else
                performance = "Fail";

            tableModel.addRow(
                    new Object[]{
                            student.name,
                            String.format("%.2f", average),
                            student.getHighest(),
                            student.getLowest(),
                            student.getGrade(),
                            performance
                    }
            );
        }

        updateStatistics();
    }

    // =========================
    // UPDATE STATISTICS
    // =========================

    private void updateStatistics() {

        if (students.isEmpty()) {

            totalStudentsLabel.setText("0");
            classAverageLabel.setText("0.00");
            highestMarkLabel.setText("0");
            topStudentLabel.setText("-");

            return;
        }

        double totalAverage = 0;
        int highest = 0;

        Student topStudent = students.get(0);

        for (Student student : students) {

            totalAverage += student.getAverage();

            highest = Math.max(
                    highest,
                    student.getHighest()
            );

            if (student.getAverage()
                    > topStudent.getAverage()) {

                topStudent = student;
            }
        }

        double classAverage =
                totalAverage / students.size();

        totalStudentsLabel.setText(
                String.valueOf(students.size())
        );

        classAverageLabel.setText(
                String.format("%.2f", classAverage)
        );

        highestMarkLabel.setText(
                String.valueOf(highest)
        );

        topStudentLabel.setText(
                topStudent.name
        );
    }

    // =========================
    // CLEAR FORM
    // =========================

    private void clearForm() {

        nameField.setText("");

        for (JTextField field : markFields) {
            field.setText("");
        }

        nameField.requestFocus();
    }

    // =========================
    // TEXT FIELD STYLE
    // =========================

    private void styleTextField(JTextField field) {

        field.setFont(
                new Font("Segoe UI", Font.PLAIN, 14)
        );

        field.setPreferredSize(
                new Dimension(100, 38)
        );

        field.setBorder(
                new CompoundBorder(
                        new LineBorder(
                                new Color(203, 213, 225),
                                1,
                                true
                        ),
                        new EmptyBorder(
                                8, 10, 8, 10
                        )
                )
        );
    }

    // =========================
    // BUTTON
    // =========================

    private JButton createButton(
            String text,
            Color color
    ) {

        JButton button = new JButton(text);

        button.setFont(
                new Font("Segoe UI", Font.BOLD, 14)
        );

        button.setForeground(Color.WHITE);
        button.setBackground(color);

        button.setFocusPainted(false);
        button.setBorderPainted(false);

        button.setCursor(
                new Cursor(Cursor.HAND_CURSOR)
        );

        button.setPreferredSize(
                new Dimension(200, 42)
        );

        return button;
    }

    // =========================
    // STATISTICS CARD
    // =========================

    private JLabel createStatCard(
            JPanel parent,
            String icon,
            String title,
            String value,
            Color color
    ) {

        JPanel card = new JPanel();

        card.setBackground(WHITE);

        card.setBorder(
                new CompoundBorder(
                        new LineBorder(
                                new Color(226, 232, 240),
                                1,
                                true
                        ),
                        new EmptyBorder(
                                12, 15, 12, 15
                        )
                )
        );

        card.setLayout(
                new BoxLayout(
                        card,
                        BoxLayout.Y_AXIS
                )
        );

        JLabel iconLabel = new JLabel(icon);

        iconLabel.setFont(
                new Font("Segoe UI Emoji",
                        Font.PLAIN, 20)
        );

        JLabel titleLabel = new JLabel(title);

        titleLabel.setFont(
                new Font("Segoe UI",
                        Font.PLAIN, 12)
        );

        titleLabel.setForeground(GRAY);

        JLabel valueLabel = new JLabel(value);

        valueLabel.setFont(
                new Font("Segoe UI",
                        Font.BOLD, 21)
        );

        valueLabel.setForeground(color);

        card.add(iconLabel);
        card.add(Box.createVerticalStrut(3));
        card.add(titleLabel);
        card.add(Box.createVerticalStrut(3));
        card.add(valueLabel);

        parent.add(card);

        return valueLabel;
    }

    // =========================
    // GRADE COLOR RENDERER
    // =========================

    static class GradeRenderer
            extends DefaultTableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(
                JTable table,
                Object value,
                boolean isSelected,
                boolean hasFocus,
                int row,
                int column
        ) {

            JLabel label = (JLabel)
                    super.getTableCellRendererComponent(
                            table,
                            value,
                            isSelected,
                            hasFocus,
                            row,
                            column
                    );

            label.setHorizontalAlignment(
                    SwingConstants.CENTER
            );

            label.setFont(
                    new Font("Segoe UI",
                            Font.BOLD, 14)
            );

            if (!isSelected) {

                String grade =
                        value.toString();

                if (grade.equals("A+")
                        || grade.equals("A")) {

                    label.setForeground(GREEN);

                } else if (grade.equals("B")
                        || grade.equals("C")) {

                    label.setForeground(BLUE);

                } else if (grade.equals("D")) {

                    label.setForeground(ORANGE);

                } else {

                    label.setForeground(RED);
                }
            }

            return label;
        }
    }

    // =========================
    // MAIN
    // =========================

    public static void main(String[] args) {

        try {

            UIManager.setLookAndFeel(
                    UIManager.getSystemLookAndFeelClassName()
            );

        } catch (Exception ignored) {
        }

        SwingUtilities.invokeLater(() -> {

            StudentGradeTracker app =
                    new StudentGradeTracker();

            app.setVisible(true);
        });
    }
}
