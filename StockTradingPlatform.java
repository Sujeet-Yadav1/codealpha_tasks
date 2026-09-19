import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class StockTradingPlatform extends JFrame {

    // ===================== COLORS =====================
    static final Color BG = new Color(15, 23, 42);
    static final Color PANEL = new Color(30, 41, 59);
    static final Color PANEL2 = new Color(51, 65, 85);
    static final Color BLUE = new Color(59, 130, 246);
    static final Color CYAN = new Color(6, 182, 212);
    static final Color GREEN = new Color(16, 185, 129);
    static final Color RED = new Color(239, 68, 68);
    static final Color PURPLE = new Color(139, 92, 246);
    static final Color ORANGE = new Color(245, 158, 11);
    static final Color TEXT = new Color(241, 245, 249);
    static final Color MUTED = new Color(148, 163, 184);
    static final Color BORDER = new Color(71, 85, 105);

    // ===================== DATA =====================
    static class Stock {
        String symbol, name, sector;
        double price, change;

        Stock(String symbol, String name, String sector, double price, double change) {
            this.symbol = symbol;
            this.name = name;
            this.sector = sector;
            this.price = price;
            this.change = change;
        }
    }

    static class Transaction {
        String type, symbol, date;
        int quantity;
        double price, total;

        Transaction(String type, String symbol, int quantity, double price) {
            this.type = type;
            this.symbol = symbol;
            this.quantity = quantity;
            this.price = price;
            this.total = quantity * price;
            this.date = LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm"));
        }
    }

    final Map<String, Stock> stocks = new LinkedHashMap<>();
    final Map<String, Integer> portfolio = new LinkedHashMap<>();
    final java.util.List<Transaction> transactions = new ArrayList<>();

    double cash = 100000;
    double totalInvested = 0;

    // ===================== UI =====================
    CardLayout pageLayout = new CardLayout();
    JPanel pages = new JPanel(pageLayout);

    JLabel pageTitle;
    JLabel cashLabel, investedLabel, portfolioLabel, profitLabel;
    JLabel availableLabel, bookedLabel, transactionLabel;
    JTextField marketSearch, quantityField;
    JComboBox<String> stockCombo;
    JTable marketTable, portfolioTable, transactionTable;
    DefaultTableModel marketModel, portfolioModel, transactionModel;

    JButton dashboardBtn, marketBtn, tradeBtn, portfolioBtn, historyBtn;

    DecimalFormat money = new DecimalFormat("#,##0.00");

    public StockTradingPlatform() {
        setTitle("TradePro | Virtual Stock Trading Platform");
        setSize(1400, 850);
        setMinimumSize(new Dimension(1100, 700));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        loadStocks();
        buildUI();
        refreshAll();
    }

    // ===================== STOCK DATA =====================
    void loadStocks() {
        stocks.put("TCS", new Stock("TCS", "Tata Consultancy Services", "IT", 3500, 2.40));
        stocks.put("INFY", new Stock("INFY", "Infosys Limited", "IT", 1800, -0.80));
        stocks.put("RELIANCE", new Stock("RELIANCE", "Reliance Industries", "Energy", 2900, 1.70));
        stocks.put("HDFCBANK", new Stock("HDFCBANK", "HDFC Bank", "Banking", 1700, 0.90));
        stocks.put("ITC", new Stock("ITC", "ITC Limited", "FMCG", 520, 2.10));
        stocks.put("WIPRO", new Stock("WIPRO", "Wipro Limited", "IT", 620, -1.20));
        stocks.put("SBIN", new Stock("SBIN", "State Bank of India", "Banking", 850, 1.45));
        stocks.put("ICICIBANK", new Stock("ICICIBANK", "ICICI Bank", "Banking", 1250, 0.72));
        stocks.put("BHARTIARTL", new Stock("BHARTIARTL", "Bharti Airtel", "Telecom", 1650, -0.35));
        stocks.put("MARUTI", new Stock("MARUTI", "Maruti Suzuki", "Auto", 12500, 1.18));
    }

    // ===================== HELPERS =====================
    JLabel label(String text, int size, Color color, int style) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", style, size));
        l.setForeground(color);
        return l;
    }

    JPanel roundedPanel() {
        JPanel p = new JPanel();
        p.setBackground(PANEL);
        p.setBorder(new CompoundBorder(
                new LineBorder(BORDER, 1, true),
                new EmptyBorder(18, 18, 18, 18)
        ));
        return p;
    }

    JButton navButton(String text) {
        JButton b = new JButton(text);
        b.setHorizontalAlignment(SwingConstants.LEFT);
        b.setForeground(MUTED);
        b.setBackground(BG);
        b.setFont(new Font("Segoe UI", Font.BOLD, 14));
        b.setBorder(new EmptyBorder(13, 18, 13, 10));
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        b.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                if (b.getBackground() != BLUE) b.setBackground(PANEL);
            }
            public void mouseExited(MouseEvent e) {
                if (b.getBackground() != BLUE) b.setBackground(BG);
            }
        });
        return b;
    }

    JButton actionButton(String text, Color color) {
        JButton b = new JButton(text);
        b.setBackground(color);
        b.setForeground(Color.WHITE);
        b.setFont(new Font("Segoe UI", Font.BOLD, 13));
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setPreferredSize(new Dimension(170, 42));
        return b;
    }

    JLabel statCard(JPanel parent, String title, String value, String subtitle, Color accent) {
        JPanel card = new JPanel(new BorderLayout(10, 8));
        card.setBackground(PANEL);
        card.setBorder(new CompoundBorder(
                new LineBorder(BORDER, 1, true),
                new EmptyBorder(16, 18, 16, 18)
        ));

        JPanel accentBar = new JPanel();
        accentBar.setBackground(accent);
        accentBar.setPreferredSize(new Dimension(5, 1));
        card.add(accentBar, BorderLayout.WEST);

        JPanel center = new JPanel();
        center.setOpaque(false);
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));

        center.add(label(title.toUpperCase(), 11, MUTED, Font.BOLD));
        center.add(Box.createVerticalStrut(6));

        JLabel valueLabel = label(value, 23, TEXT, Font.BOLD);
        center.add(valueLabel);

        center.add(Box.createVerticalStrut(4));
        center.add(label(subtitle, 11, MUTED, Font.PLAIN));

        card.add(center, BorderLayout.CENTER);
        parent.add(card);
        return valueLabel;
    }

    void styleTable(JTable table) {
        table.setRowHeight(38);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setForeground(TEXT);
        table.setBackground(PANEL);
        table.setSelectionBackground(new Color(30, 64, 175));
        table.setSelectionForeground(Color.WHITE);
        table.setGridColor(BORDER);
        table.setShowVerticalLines(false);

        JTableHeader header = table.getTableHeader();
        header.setBackground(PANEL2);
        header.setForeground(TEXT);
        header.setFont(new Font("Segoe UI", Font.BOLD, 12));
        header.setPreferredSize(new Dimension(0, 40));
    }

    // ===================== MAIN UI =====================
    void buildUI() {
        setLayout(new BorderLayout());

        add(buildSidebar(), BorderLayout.WEST);
        add(buildMainArea(), BorderLayout.CENTER);
    }

    JPanel buildSidebar() {
        JPanel side = new JPanel(new BorderLayout());
        side.setBackground(BG);
        side.setPreferredSize(new Dimension(235, 0));
        side.setBorder(new MatteBorder(0, 0, 0, 1, BORDER));

        JPanel top = new JPanel();
        top.setBackground(BG);
        top.setBorder(new EmptyBorder(25, 20, 20, 20));
        top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));

        JLabel logo = label("◆  TRADEPRO", 22, Color.WHITE, Font.BOLD);
        JLabel version = label("VIRTUAL INVESTMENT", 10, CYAN, Font.BOLD);
        top.add(logo);
        top.add(Box.createVerticalStrut(5));
        top.add(version);

        JPanel nav = new JPanel();
        nav.setBackground(BG);
        nav.setBorder(new EmptyBorder(10, 10, 10, 10));
        nav.setLayout(new BoxLayout(nav, BoxLayout.Y_AXIS));

        dashboardBtn = navButton("▣   Dashboard");
        marketBtn = navButton("◈   Market Watch");
        tradeBtn = navButton("↕   Buy / Sell");
        portfolioBtn = navButton("▤   My Portfolio");
        historyBtn = navButton("◷   Transactions");

        dashboardBtn.addActionListener(e -> showPage("dashboard", dashboardBtn));
        marketBtn.addActionListener(e -> showPage("market", marketBtn));
        tradeBtn.addActionListener(e -> showPage("trade", tradeBtn));
        portfolioBtn.addActionListener(e -> showPage("portfolio", portfolioBtn));
        historyBtn.addActionListener(e -> showPage("history", historyBtn));

        nav.add(dashboardBtn);
        nav.add(marketBtn);
        nav.add(tradeBtn);
        nav.add(portfolioBtn);
        nav.add(historyBtn);

        side.add(top, BorderLayout.NORTH);
        side.add(nav, BorderLayout.CENTER);

        JPanel bottom = new JPanel();
        bottom.setBackground(BG);
        bottom.setBorder(new EmptyBorder(15, 15, 20, 15));
        bottom.setLayout(new BoxLayout(bottom, BoxLayout.Y_AXIS));

        JPanel account = new JPanel();
        account.setBackground(PANEL);
        account.setBorder(new CompoundBorder(
                new LineBorder(BORDER, 1, true),
                new EmptyBorder(12, 12, 12, 12)
        ));
        account.setLayout(new BoxLayout(account, BoxLayout.Y_AXIS));
        account.add(label("DEMO ACCOUNT", 10, CYAN, Font.BOLD));
        account.add(Box.createVerticalStrut(5));
        account.add(label("Investor", 14, TEXT, Font.BOLD));
        account.add(Box.createVerticalStrut(3));
        account.add(label("Starting Balance", 11, MUTED, Font.PLAIN));
        account.add(label("₹1,00,000.00", 14, GREEN, Font.BOLD));

        bottom.add(account);
        bottom.add(Box.createVerticalStrut(12));

        JButton exit = navButton("⏻   Exit Application");
        exit.addActionListener(e -> System.exit(0));
        bottom.add(exit);

        side.add(bottom, BorderLayout.SOUTH);
        return side;
    }

    JPanel buildMainArea() {
        JPanel main = new JPanel(new BorderLayout());
        main.setBackground(BG);

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(BG);
        header.setBorder(new EmptyBorder(22, 28, 18, 28));

        pageTitle = label("Dashboard", 26, TEXT, Font.BOLD);
        header.add(pageTitle, BorderLayout.WEST);

        JPanel status = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        status.setOpaque(false);

        JLabel live = label("●  MARKET SIMULATOR", 11, GREEN, Font.BOLD);
        status.add(live);
        header.add(status, BorderLayout.EAST);

        main.add(header, BorderLayout.NORTH);

        pages.setBackground(BG);
        pages.add(buildDashboard(), "dashboard");
        pages.add(buildMarketPage(), "market");
        pages.add(buildTradePage(), "trade");
        pages.add(buildPortfolioPage(), "portfolio");
        pages.add(buildHistoryPage(), "history");

        main.add(pages, BorderLayout.CENTER);
        return main;
    }

    // ===================== DASHBOARD =====================
    JPanel buildDashboard() {
        JPanel root = pagePanel();
        root.setLayout(new BorderLayout(15, 15));

        JPanel stats = new JPanel(new GridLayout(1, 4, 12, 0));
        stats.setOpaque(false);

        cashLabel = statCard(stats, "Available Cash", "₹1,00,000", "Ready to invest", GREEN);
        investedLabel = statCard(stats, "Total Invested", "₹0", "Cost of holdings", BLUE);
        portfolioLabel = statCard(stats, "Portfolio Value", "₹0", "Current market value", PURPLE);
        profitLabel = statCard(stats, "Total P / L", "₹0", "Overall performance", CYAN);

        root.add(stats, BorderLayout.NORTH);

        JPanel center = new JPanel(new GridLayout(1, 2, 15, 0));
        center.setOpaque(false);

        center.add(buildMarketPreview());
        center.add(buildQuickTrade());

        root.add(center, BorderLayout.CENTER);
        root.add(buildRecentTransactions(), BorderLayout.SOUTH);

        return root;
    }

    JPanel buildMarketPreview() {
        JPanel p = roundedPanel();
        p.setLayout(new BorderLayout(10, 10));

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.add(label("Market Overview", 19, TEXT, Font.BOLD), BorderLayout.WEST);
        top.add(label("Simulated prices", 11, MUTED, Font.PLAIN), BorderLayout.EAST);
        p.add(top, BorderLayout.NORTH);

        String[] cols = {"SYMBOL", "COMPANY", "PRICE", "CHANGE"};
        marketModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        marketTable = new JTable(marketModel);
        styleTable(marketTable);

        marketTable.getColumnModel().getColumn(0).setPreferredWidth(90);
        marketTable.getColumnModel().getColumn(1).setPreferredWidth(190);
        marketTable.getColumnModel().getColumn(2).setPreferredWidth(110);
        marketTable.getColumnModel().getColumn(3).setPreferredWidth(100);

        marketTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable t, Object value,
                    boolean selected, boolean focus, int row, int column) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(
                        t, value, selected, focus, row, column);
                l.setBorder(new EmptyBorder(0, 8, 0, 8));
                if (column == 3 && value != null) {
                    String s = value.toString();
                    l.setForeground(s.startsWith("+") ? GREEN : RED);
                    l.setFont(new Font("Segoe UI", Font.BOLD, 13));
                } else {
                    l.setForeground(selected ? Color.WHITE : TEXT);
                }
                return l;
            }
        });

        p.add(new JScrollPane(marketTable), BorderLayout.CENTER);
        return p;
    }

    JPanel buildQuickTrade() {
        JPanel p = roundedPanel();
        p.setLayout(new BorderLayout());

        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setBorder(new EmptyBorder(5, 5, 5, 5));
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        content.add(label("Quick Trade", 20, TEXT, Font.BOLD));
        content.add(Box.createVerticalStrut(4));
        content.add(label("Execute a simulated market order", 11, MUTED, Font.PLAIN));
        content.add(Box.createVerticalStrut(20));

        content.add(label("SELECT STOCK", 11, MUTED, Font.BOLD));
        stockCombo = new JComboBox<>(stocks.keySet().toArray(new String[0]));
        styleCombo(stockCombo);
        content.add(stockCombo);

        content.add(Box.createVerticalStrut(15));
        content.add(label("QUANTITY", 11, MUTED, Font.BOLD));

        quantityField = new JTextField();
        styleField(quantityField);
        content.add(quantityField);

        content.add(Box.createVerticalStrut(18));

        JPanel buttons = new JPanel(new GridLayout(1, 2, 10, 0));
        buttons.setOpaque(false);

        JButton buy = actionButton("BUY STOCK", GREEN);
        JButton sell = actionButton("SELL STOCK", RED);

        buy.addActionListener(e -> executeTrade(true));
        sell.addActionListener(e -> executeTrade(false));

        buttons.add(buy);
        buttons.add(sell);
        content.add(buttons);

        content.add(Box.createVerticalStrut(20));

        JPanel info = new JPanel();
        info.setBackground(new Color(15, 23, 42));
        info.setBorder(new EmptyBorder(12, 12, 12, 12));
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        info.add(label("TRADING MODE", 10, CYAN, Font.BOLD));
        info.add(Box.createVerticalStrut(5));
        info.add(label("Virtual money only", 13, TEXT, Font.BOLD));
        info.add(label("No real transactions are made.", 11, MUTED, Font.PLAIN));

        content.add(info);
        p.add(content, BorderLayout.CENTER);
        return p;
    }

    JPanel buildRecentTransactions() {
        JPanel p = roundedPanel();
        p.setLayout(new BorderLayout(10, 8));
        p.setPreferredSize(new Dimension(0, 165));

        p.add(label("Recent Activity", 18, TEXT, Font.BOLD), BorderLayout.NORTH);

        transactionModel = new DefaultTableModel(
                new Object[]{"TYPE", "STOCK", "QTY", "PRICE", "TOTAL", "DATE"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        transactionTable = new JTable(transactionModel);
        styleTable(transactionTable);

        p.add(new JScrollPane(transactionTable), BorderLayout.CENTER);
        return p;
    }

    // ===================== MARKET PAGE =====================
    JPanel buildMarketPage() {
        JPanel root = pagePanel();
        root.setLayout(new BorderLayout(15, 15));

        JPanel top = roundedPanel();
        top.setLayout(new BorderLayout(10, 0));

        JPanel text = new JPanel();
        text.setOpaque(false);
        text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));
        text.add(label("Market Watch", 20, TEXT, Font.BOLD));
        text.add(label("Explore all available simulated stocks", 11, MUTED, Font.PLAIN));

        marketSearch = new JTextField();
        styleField(marketSearch);
        marketSearch.setPreferredSize(new Dimension(300, 40));
        marketSearch.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                refreshMarketTable();
            }
        });

        top.add(text, BorderLayout.WEST);
        top.add(marketSearch, BorderLayout.EAST);

        root.add(top, BorderLayout.NORTH);

        JPanel tablePanel = roundedPanel();
        tablePanel.setLayout(new BorderLayout());

        if (marketModel == null) {
            marketModel = new DefaultTableModel(
                    new Object[]{"SYMBOL", "COMPANY", "SECTOR", "PRICE", "CHANGE"}, 0) {
                public boolean isCellEditable(int r, int c) { return false; }
            };
        }

        JTable fullMarket = new JTable(marketModel);
        styleTable(fullMarket);
        tablePanel.add(new JScrollPane(fullMarket), BorderLayout.CENTER);

        root.add(tablePanel, BorderLayout.CENTER);
        return root;
    }

    // ===================== TRADE PAGE =====================
    JPanel buildTradePage() {
        JPanel root = pagePanel();
        root.setLayout(new GridLayout(1, 2, 18, 0));

        JPanel left = roundedPanel();
        left.setLayout(new BorderLayout());

        JPanel heading = new JPanel();
        heading.setOpaque(false);
        heading.setLayout(new BoxLayout(heading, BoxLayout.Y_AXIS));
        heading.add(label("Place Order", 22, TEXT, Font.BOLD));
        heading.add(Box.createVerticalStrut(5));
        heading.add(label("Buy or sell shares instantly", 12, MUTED, Font.PLAIN));

        left.add(heading, BorderLayout.NORTH);

        JPanel form = new JPanel();
        form.setOpaque(false);
        form.setBorder(new EmptyBorder(25, 5, 5, 5));
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));

        form.add(label("STOCK", 11, MUTED, Font.BOLD));
        JComboBox<String> combo = new JComboBox<>(stocks.keySet().toArray(new String[0]));
        styleCombo(combo);
        form.add(combo);

        form.add(Box.createVerticalStrut(18));
        form.add(label("QUANTITY", 11, MUTED, Font.BOLD));
        JTextField qty = new JTextField();
        styleField(qty);
        form.add(qty);

        form.add(Box.createVerticalStrut(18));

        JLabel estimated = label("Estimated Order Value: ₹0.00", 15, CYAN, Font.BOLD);
        form.add(estimated);

        combo.addActionListener(e -> updateEstimate(combo, qty, estimated));
        qty.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                updateEstimate(combo, qty, estimated);
            }
        });

        form.add(Box.createVerticalStrut(22));

        JPanel bs = new JPanel(new GridLayout(1, 2, 12, 0));
        bs.setOpaque(false);

        JButton buy = actionButton("▲  BUY", GREEN);
        JButton sell = actionButton("▼  SELL", RED);

        buy.addActionListener(e -> executeTrade(combo, qty, true));
        sell.addActionListener(e -> executeTrade(combo, qty, false));

        bs.add(buy);
        bs.add(sell);
        form.add(bs);

        form.add(Box.createVerticalStrut(25));

        JPanel rules = new JPanel();
        rules.setBackground(new Color(15, 23, 42));
        rules.setBorder(new EmptyBorder(15, 15, 15, 15));
        rules.setLayout(new BoxLayout(rules, BoxLayout.Y_AXIS));
        rules.add(label("TRADING RULES", 11, ORANGE, Font.BOLD));
        rules.add(Box.createVerticalStrut(8));
        rules.add(label("• Buy orders reduce available cash.", 12, MUTED, Font.PLAIN));
        rules.add(label("• Sell orders require sufficient shares.", 12, MUTED, Font.PLAIN));
        rules.add(label("• All prices are simulated.", 12, MUTED, Font.PLAIN));
        rules.add(label("• Starting capital: ₹1,00,000.", 12, MUTED, Font.PLAIN));

        form.add(rules);
        left.add(form, BorderLayout.CENTER);

        JPanel right = roundedPanel();
        right.setLayout(new BorderLayout(10, 10));

        right.add(label("Selected Stock", 20, TEXT, Font.BOLD), BorderLayout.NORTH);

        JPanel stockInfo = new JPanel();
        stockInfo.setOpaque(false);
        stockInfo.setLayout(new BoxLayout(stockInfo, BoxLayout.Y_AXIS));
        stockInfo.setBorder(new EmptyBorder(20, 10, 10, 10));

        JLabel bigSymbol = label("TCS", 40, CYAN, Font.BOLD);
        JLabel bigName = label("Tata Consultancy Services", 16, TEXT, Font.BOLD);
        JLabel bigPrice = label("₹3,500.00", 30, GREEN, Font.BOLD);

        stockInfo.add(bigSymbol);
        stockInfo.add(Box.createVerticalStrut(5));
        stockInfo.add(bigName);
        stockInfo.add(Box.createVerticalStrut(20));
        stockInfo.add(label("CURRENT MARKET PRICE", 10, MUTED, Font.BOLD));
        stockInfo.add(bigPrice);
        stockInfo.add(Box.createVerticalStrut(12));
        stockInfo.add(label("+2.40% today", 14, GREEN, Font.BOLD));

        combo.addActionListener(e -> {
            Stock s = stocks.get(combo.getSelectedItem());
            if (s != null) {
                bigSymbol.setText(s.symbol);
                bigName.setText(s.name);
                bigPrice.setText("₹" + money.format(s.price));
                bigPrice.setForeground(s.change >= 0 ? GREEN : RED);
            }
        });

        right.add(stockInfo, BorderLayout.CENTER);

        root.add(left);
        root.add(right);
        return root;
    }

    // ===================== PORTFOLIO PAGE =====================
    JPanel buildPortfolioPage() {
        JPanel root = pagePanel();
        root.setLayout(new BorderLayout(15, 15));

        JPanel top = new JPanel(new GridLayout(1, 3, 12, 0));
        top.setOpaque(false);

        availableLabel = statCard(top, "Cash Balance", "₹0", "Uninvested cash", GREEN);
        bookedLabel = statCard(top, "Holdings", "0", "Different stocks", BLUE);
        transactionLabel = statCard(top, "Orders", "0", "Completed transactions", PURPLE);

        root.add(top, BorderLayout.NORTH);

        JPanel tablePanel = roundedPanel();
        tablePanel.setLayout(new BorderLayout(10, 10));
        tablePanel.add(label("Current Holdings", 19, TEXT, Font.BOLD), BorderLayout.NORTH);

        portfolioModel = new DefaultTableModel(
                new Object[]{"SYMBOL", "COMPANY", "SHARES", "AVG / BUY", "MARKET PRICE", "VALUE", "P / L"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        portfolioTable = new JTable(portfolioModel);
        styleTable(portfolioTable);

        tablePanel.add(new JScrollPane(portfolioTable), BorderLayout.CENTER);
        root.add(tablePanel, BorderLayout.CENTER);
        return root;
    }

    // ===================== HISTORY PAGE =====================
    JPanel buildHistoryPage() {
        JPanel root = pagePanel();
        root.setLayout(new BorderLayout(15, 15));

        JPanel heading = roundedPanel();
        heading.setLayout(new BorderLayout());
        heading.add(label("Transaction History", 20, TEXT, Font.BOLD), BorderLayout.WEST);
        heading.add(label("Every simulated order is recorded here", 11, MUTED, Font.PLAIN), BorderLayout.EAST);

        root.add(heading, BorderLayout.NORTH);

        JPanel tablePanel = roundedPanel();
        tablePanel.setLayout(new BorderLayout());

        transactionModel = new DefaultTableModel(
                new Object[]{"TYPE", "SYMBOL", "QUANTITY", "PRICE", "TOTAL", "DATE"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        transactionTable = new JTable(transactionModel);
        styleTable(transactionTable);

        transactionTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean selected, boolean focus, int row, int column) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(
                        table, value, selected, focus, row, column);
                l.setBorder(new EmptyBorder(0, 8, 0, 8));
                if (column == 0 && value != null) {
                    l.setForeground("BUY".equals(value.toString()) ? GREEN : RED);
                    l.setFont(new Font("Segoe UI", Font.BOLD, 12));
                } else {
                    l.setForeground(selected ? Color.WHITE : TEXT);
                }
                return l;
            }
        });

        tablePanel.add(new JScrollPane(transactionTable), BorderLayout.CENTER);
        root.add(tablePanel, BorderLayout.CENTER);
        return root;
    }

    // ===================== STYLING =====================
    JPanel pagePanel() {
        JPanel p = new JPanel();
        p.setBackground(BG);
        p.setBorder(new EmptyBorder(0, 25, 25, 25));
        return p;
    }

    void styleField(JTextField field) {
        field.setBackground(PANEL2);
        field.setForeground(TEXT);
        field.setCaretColor(TEXT);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBorder(new CompoundBorder(
                new LineBorder(BORDER, 1, true),
                new EmptyBorder(8, 10, 8, 10)
        ));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
    }

    void styleCombo(JComboBox<String> combo) {
        combo.setBackground(PANEL2);
        combo.setForeground(TEXT);
        combo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        combo.setBorder(new LineBorder(BORDER, 1, true));
        combo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
    }

    void showPage(String page, JButton active) {
        pages.show(pageLayout, page);

        String title = "Dashboard";
        if (page.equals("market")) title = "Market Watch";
        if (page.equals("trade")) title = "Buy / Sell";
        if (page.equals("portfolio")) title = "My Portfolio";
        if (page.equals("history")) title = "Transactions";

        pageTitle.setText(title);

        JButton[] all = {dashboardBtn, marketBtn, tradeBtn, portfolioBtn, historyBtn};
        for (JButton b : all) {
            b.setBackground(BG);
            b.setForeground(MUTED);
        }

        active.setBackground(BLUE);
        active.setForeground(Color.WHITE);
    }

    // ===================== TRADE LOGIC =====================
    void executeTrade(boolean buy) {
        executeTrade(stockCombo, quantityField, buy);
    }

    void executeTrade(JComboBox<String> combo, JTextField qtyField, boolean buy) {
        try {
            String symbol = (String) combo.getSelectedItem();
            int qty = Integer.parseInt(qtyField.getText().trim());

            if (qty <= 0) throw new NumberFormatException();

            Stock stock = stocks.get(symbol);
            double total = stock.price * qty;

            if (buy) {
                if (total > cash) {
                    JOptionPane.showMessageDialog(
                            this,
                            "You do not have enough cash.\nRequired: ₹" +
                                    money.format(total) +
                                    "\nAvailable: ₹" +
                                    money.format(cash),
                            "Insufficient Cash",
                            JOptionPane.WARNING_MESSAGE
                    );
                    return;
                }

                cash -= total;
                totalInvested += total;
                portfolio.put(symbol, portfolio.getOrDefault(symbol, 0) + qty);
                transactions.add(new Transaction("BUY", symbol, qty, stock.price));

                JOptionPane.showMessageDialog(
                        this,
                        "BUY ORDER COMPLETED\n\n" +
                                qty + " shares of " + symbol +
                                "\nPrice: ₹" + money.format(stock.price) +
                                "\nTotal: ₹" + money.format(total),
                        "Trade Successful",
                        JOptionPane.INFORMATION_MESSAGE
                );

            } else {
                int owned = portfolio.getOrDefault(symbol, 0);

                if (owned < qty) {
                    JOptionPane.showMessageDialog(
                            this,
                            "You only own " + owned + " shares of " + symbol + ".",
                            "Insufficient Shares",
                            JOptionPane.WARNING_MESSAGE
                    );
                    return;
                }

                cash += total;

                double averageCost = getAverageBuyPrice(symbol);
                totalInvested -= averageCost * qty;
                if (totalInvested < 0) totalInvested = 0;

                if (owned == qty) portfolio.remove(symbol);
                else portfolio.put(symbol, owned - qty);

                transactions.add(new Transaction("SELL", symbol, qty, stock.price));

                JOptionPane.showMessageDialog(
                        this,
                        "SELL ORDER COMPLETED\n\n" +
                                qty + " shares of " + symbol +
                                "\nPrice: ₹" + money.format(stock.price) +
                                "\nReceived: ₹" + money.format(total),
                        "Trade Successful",
                        JOptionPane.INFORMATION_MESSAGE
                );
            }

            qtyField.setText("");
            refreshAll();

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please enter a valid positive whole number for quantity.",
                    "Invalid Quantity",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    double getAverageBuyPrice(String symbol) {
        double boughtValue = 0;
        int boughtQty = 0;

        for (Transaction t : transactions) {
            if (!t.symbol.equals(symbol)) continue;

            if ("BUY".equals(t.type)) {
                boughtValue += t.total;
                boughtQty += t.quantity;
            } else {
                int q = Math.min(t.quantity, boughtQty);
                if (boughtQty > 0) {
                    double avg = boughtValue / boughtQty;
                    boughtValue -= avg * q;
                    boughtQty -= q;
                }
            }
        }

        return boughtQty == 0 ? stocks.get(symbol).price : boughtValue / boughtQty;
    }

    void updateEstimate(JComboBox<String> combo, JTextField qty, JLabel label) {
        try {
            Stock s = stocks.get(combo.getSelectedItem());
            int q = Integer.parseInt(qty.getText().trim());
            label.setText("Estimated Order Value: ₹" + money.format(s.price * q));
        } catch (Exception e) {
            label.setText("Estimated Order Value: ₹0.00");
        }
    }

    // ===================== REFRESH =====================
    void refreshAll() {
        refreshMarketTable();
        refreshPortfolio();
        refreshTransactions();
        refreshDashboardStats();
    }

    void refreshMarketTable() {
        if (marketModel == null) return;

        String search = marketSearch == null
                ? ""
                : marketSearch.getText().trim().toLowerCase();

        marketModel.setRowCount(0);

        for (Stock s : stocks.values()) {
            if (search.isEmpty()
                    || s.symbol.toLowerCase().contains(search)
                    || s.name.toLowerCase().contains(search)
                    || s.sector.toLowerCase().contains(search)) {

                marketModel.addRow(new Object[]{
                        s.symbol,
                        s.name,
                        s.sector,
                        "₹" + money.format(s.price),
                        (s.change >= 0 ? "+" : "") +
                                money.format(s.change) + "%"
                });
            }
        }
    }

    void refreshPortfolio() {
        if (portfolioModel == null) return;

        portfolioModel.setRowCount(0);

        for (Map.Entry<String, Integer> entry : portfolio.entrySet()) {
            String symbol = entry.getKey();
            int shares = entry.getValue();
            Stock s = stocks.get(symbol);

            double avg = getAverageBuyPrice(symbol);
            double value = s.price * shares;
            double pnl = (s.price - avg) * shares;

            portfolioModel.addRow(new Object[]{
                    symbol,
                    s.name,
                    shares,
                    "₹" + money.format(avg),
                    "₹" + money.format(s.price),
                    "₹" + money.format(value),
                    (pnl >= 0 ? "+₹" : "-₹") + money.format(Math.abs(pnl))
            });
        }

        if (availableLabel != null) availableLabel.setText("₹" + money.format(cash));
        if (bookedLabel != null) bookedLabel.setText(String.valueOf(portfolio.size()));
        if (transactionLabel != null) transactionLabel.setText(String.valueOf(transactions.size()));
    }

    void refreshTransactions() {
        if (transactionModel == null) return;

        transactionModel.setRowCount(0);

        int start = Math.max(0, transactions.size() - 8);

        for (int i = start; i < transactions.size(); i++) {
            Transaction t = transactions.get(i);
            transactionModel.addRow(new Object[]{
                    t.type,
                    t.symbol,
                    t.quantity,
                    "₹" + money.format(t.price),
                    "₹" + money.format(t.total),
                    t.date
            });
        }
    }

    void refreshDashboardStats() {
        double marketValue = 0;

        for (Map.Entry<String, Integer> e : portfolio.entrySet()) {
            Stock s = stocks.get(e.getKey());
            marketValue += s.price * e.getValue();
        }

        double pnl = cash + marketValue - 100000;

        if (cashLabel != null) cashLabel.setText("₹" + money.format(cash));
        if (investedLabel != null) investedLabel.setText("₹" + money.format(totalInvested));
        if (portfolioLabel != null) portfolioLabel.setText("₹" + money.format(marketValue));

        if (profitLabel != null) {
            profitLabel.setText((pnl >= 0 ? "+₹" : "-₹") + money.format(Math.abs(pnl)));
            profitLabel.setForeground(pnl >= 0 ? GREEN : RED);
        }
    }

    // ===================== MAIN =====================
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        SwingUtilities.invokeLater(() -> {
            StockTradingPlatform app = new StockTradingPlatform();
            app.setVisible(true);
        });
    }
}
