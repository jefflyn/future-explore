package com.guru.future.common.ui;

import com.google.common.base.Strings;
import com.guru.future.common.cache.LiveDataCache;
import com.guru.future.common.entity.vo.FutureLiveVO;
import lombok.extern.slf4j.Slf4j;

import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.ListCellRenderer;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * @author j
 */
@Slf4j
public class FutureFrame {
    private static JFrame frame = null;
    private static DefaultListModel<JLabel> priceModel = new DefaultListModel<>();
    private static DefaultListModel<JLabel> highTopModel = new DefaultListModel<>();
    private static DefaultListModel<JLabel> lowTopModel = new DefaultListModel<>();
    private static DefaultListModel<JLabel> changeHighTopModel = new DefaultListModel<>();
    private static DefaultListModel<JLabel> changeLowTopModel = new DefaultListModel<>();
    private static String overview;

    private FutureFrame() {
    }

    public static FutureFrame buildFutureFrame(String overviewStr) {
        if (!Strings.isNullOrEmpty(overviewStr)) {
            overview = overviewStr;
        }
        return new FutureFrame();
    }

    private void buildTopModel() {
        highTopModel = new DefaultListModel<>();
        lowTopModel = new DefaultListModel<>();
        for (int i = 0; i < LiveDataCache.getPositionHighTop10().size(); i++) {
            FutureLiveVO highTop = LiveDataCache.getPositionHighTop10().get(i);
            JLabel label = new JLabel();
            label.setText(highTop.toString());
            highTopModel.add(i, label);
        }
        for (int i = 0; i < LiveDataCache.getPositionLowTop10().size(); i++) {
            FutureLiveVO lowTop = LiveDataCache.getPositionLowTop10().get(i);
            JLabel label = new JLabel();
            label.setText(lowTop.toString());
            lowTopModel.add(i, label);
        }
        changeHighTopModel = new DefaultListModel<>();
        changeLowTopModel = new DefaultListModel<>();
        for (int i = 0; i < LiveDataCache.getChangeHighTop10().size(); i++) {
            FutureLiveVO highTop = LiveDataCache.getChangeHighTop10().get(i);
            JLabel label = new JLabel();
            label.setText(highTop.toString() + " 做多");
            changeHighTopModel.add(i, label);
        }
        for (int i = 0; i < LiveDataCache.getChangeLowTop10().size(); i++) {
            FutureLiveVO lowTop = LiveDataCache.getChangeLowTop10().get(i);
            JLabel label = new JLabel();
            label.setText(lowTop.toString());
            changeLowTopModel.add(i, label);
        }
    }

    public void createMsgFrame(String content) {
        try {
//          Runtime.getRuntime().exec("say ");
            if (!Strings.isNullOrEmpty(content)) {
                JLabel label = new JLabel();
                label.setText(content);
                if (priceModel.size() > 30) {
                    Object lastOne = priceModel.lastElement();
                    priceModel.removeElement(lastOne);
                }
                priceModel.add(0, label);
            }
            buildTopModel();
            if (frame == null) {
                frame = new JFrame();
                frame.setLayout(new FlowLayout());
                frame.setBounds(0, 1000, 530, 390);
                frame.addWindowListener(new WindowAdapter() {
                    @Override
                    public void windowClosing(WindowEvent e) {
                        frame.dispose();
//                        Container c = frame.getContentPane();
//                        JTabbedPane tabbedPane = (JTabbedPane) c.getComponent(0);
//                        for (Component component : tabbedPane.getComponents()) {
//                            JPanel panel = (JPanel) component;
//                            panel.removeAll();
//                        }
                    }
                });
                JTabbedPane tabbedPane = new JTabbedPane();
                tabbedPane.addTab("trade log", createPanel());
                tabbedPane.addTab("overview", createPanel());
                frame.add(tabbedPane);
                refreshContentPane();
                frame.setVisible(true);
            } else {
                refreshContentPane();
            }
            frame.validate();
//            if (Boolean.FALSE.equals(frame.isVisible())) {
//                frame.setVisible(true);
//            }
        } catch (Exception e) {
            log.error("create trade log frame failed, error={}", e);
        }
    }

    private JPanel createPanel() {
        JPanel panel = new JPanel(false);
//        panel.setLayout(new GridLayout(3, 1));
        panel.setPreferredSize(new Dimension(520, 360));
        return panel;
    }

    /**
     * 刷新面板数据
     */
    private void refreshContentPane() {
        JList list = new JList(priceModel);
        list.setCellRenderer(new MyListCellRenderer());
        JScrollPane pricePane = new JScrollPane(list);
        pricePane.setPreferredSize(new Dimension(520, 130));

        JList highTopList = new JList(highTopModel);
        highTopList.setCellRenderer(new MyListCellRenderer());
        JScrollPane highTopPane = new JScrollPane(highTopList);
        highTopPane.setPreferredSize(new Dimension(520, 85));

        JList lowTopList = new JList(lowTopModel);
        lowTopList.setCellRenderer(new MyListCellRenderer());
        JScrollPane lowTopPane = new JScrollPane(lowTopList);
        lowTopPane.setPreferredSize(new Dimension(520, 85));

        Container c = frame.getContentPane();
        JTabbedPane tabbedPane = (JTabbedPane) c.getComponent(0);
        JPanel logPanel = (JPanel) tabbedPane.getComponent(0);
        logPanel.removeAll();
        logPanel.add(pricePane, 0);
        logPanel.add(highTopPane, 1);
        logPanel.add(lowTopPane, 2);

        // overview tab
        JTextArea overviewTxt = new JTextArea(overview);
        overviewTxt.setEditable(false);
        JScrollPane overviewPane = new JScrollPane(overviewTxt);
        overviewPane.setPreferredSize(new Dimension(520, 130));

        JList changeHighTopList = new JList(changeHighTopModel);
        changeHighTopList.setCellRenderer(new MyListCellRenderer());
        JScrollPane changeHighTopPane = new JScrollPane(changeHighTopList);
        changeHighTopPane.setPreferredSize(new Dimension(520, 85));

        JList changeLowTopList = new JList(changeLowTopModel);
        changeLowTopList.setCellRenderer(new MyListCellRenderer());
        JScrollPane changeLowTopPane = new JScrollPane(changeLowTopList);
        changeLowTopPane.setPreferredSize(new Dimension(520, 85));

        JPanel viewPanel = (JPanel) tabbedPane.getComponent(1);
        viewPanel.removeAll();
        viewPanel.add(overviewPane, 0);
        viewPanel.add(changeHighTopPane, 1);
        viewPanel.add(changeLowTopPane, 2);
    }

    public static class MyListCellRenderer extends JLabel implements ListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
                                                      boolean cellHasFocus) {
            // 设置JLable的文字
            JLabel text = (JLabel) value;
            setText(text.getText());
            if (text.getText().contains("多")
                    || text.getText().contains(LiveDataCache.SYMBOL_UP)) {
                setForeground(Color.RED);
            } else if (text.getText().contains(LiveDataCache.SYMBOL_DOWN)) {
                setForeground(Color.GREEN);
            } else {
                setForeground(Color.BLACK);
            }
            return this;
        }
    }
}
