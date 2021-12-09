package com.guru.future.common.utils;

import com.google.common.base.Strings;
import com.guru.future.biz.service.FutureLiveService;
import com.guru.future.common.cache.LiveDataCache;
import com.guru.future.common.entity.vo.FutureLiveVO;
import com.guru.future.common.entity.vo.FutureOverviewVO;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;
import javax.swing.DefaultListModel;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextPane;
import javax.swing.ListCellRenderer;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * @author j
 */
@Slf4j
public class WindowUtil {
    private static JFrame frame = null;
    private static DefaultListModel<JLabel> priceModel = new DefaultListModel<>();
    private static DefaultListModel<JLabel> highTopModel = new DefaultListModel<>();
    private static DefaultListModel<JLabel> lowTopModel = new DefaultListModel<>();
    @Resource
    private static FutureLiveService futureLiveService;

    private static void buildTopModel() {
        highTopModel = new DefaultListModel<>();
        lowTopModel = new DefaultListModel<>();
        for (int i = 0; i < LiveDataCache.getHighTop10().size(); i++) {
            FutureLiveVO highTop = LiveDataCache.getHighTop10().get(i);
            JLabel label = new JLabel();
            label.setText(highTop.toString());
            highTopModel.add(i, label);
        }
        for (int i = 0; i < LiveDataCache.getLowTop10().size(); i++) {
            FutureLiveVO lowTop = LiveDataCache.getLowTop10().get(i);
            JLabel label = new JLabel();
            label.setText(lowTop.toString());
            lowTopModel.add(i, label);
        }
    }

    public static void createMsgFrame(String content) {
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
                frame = new JFrame("Trade Log");
                frame.setLayout(new FlowLayout());
                frame.setBounds(0, 1000, 530, 358);
                frame.addWindowListener(new WindowAdapter() {
                    @Override
                    public void windowClosing(WindowEvent e) {
                        frame.dispose();
                        frame.getContentPane().removeAll();
                    }
                });
                JTabbedPane tabbedPane = new JTabbedPane();
                tabbedPane.addTab("log", createPanel());
                tabbedPane.addTab("view", createPanel());
                frame.add(tabbedPane);
                refreshContentPane();
                frame.setVisible(true);
            } else {
                refreshContentPane();
            }
            frame.validate();
        } catch (Exception e) {
            log.error("create trade log frame failed, error={}", e);
        }
    }

    private static JPanel createPanel() {
        JPanel panel = new JPanel(false);
        panel.setLayout(new GridLayout(3, 1));
        return panel;
    }

    private static void refreshContentPane() {
        JList list = new JList(priceModel);
        list.setCellRenderer(new MyListCellRenderer());
        JScrollPane pricePane = new JScrollPane(list);
        pricePane.setPreferredSize(new Dimension(520, 130));

        JList highTopList = new JList(highTopModel);
        highTopList.setCellRenderer(new MyListCellRenderer());
        JScrollPane highTopPane = new JScrollPane(highTopList);
        highTopPane.setPreferredSize(new Dimension(520, 100));

        JList lowTopList = new JList(lowTopModel);
        lowTopList.setCellRenderer(new MyListCellRenderer());
        JScrollPane lowTopPane = new JScrollPane(lowTopList);
        lowTopPane.setPreferredSize(new Dimension(520, 100));

        Container c = frame.getContentPane();
        JTabbedPane tabbedPane = (JTabbedPane) c.getComponent(0);
        JPanel logPanel = (JPanel) tabbedPane.getComponent(0);
        logPanel.removeAll();
        logPanel.add(pricePane, 0);
        logPanel.add(highTopPane, 1);
        logPanel.add(lowTopPane, 2);

        JPanel viewPanel = (JPanel) tabbedPane.getComponent(1);
        viewPanel.removeAll();
        JTextPane txtPane = new JTextPane();
        FutureOverviewVO overviewVO = futureLiveService.getMarketOverview();
        txtPane.setText(overviewVO.toString());
        viewPanel.add(txtPane);
    }

    public static class MyListCellRenderer extends JLabel implements ListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
                                                      boolean cellHasFocus) {
            // 设置JLable的文字
            JLabel text = (JLabel) value;
            setText(text.getText());
            if (text.getText().contains("多") || text.getText().contains(LiveDataCache.SYMBOL_UP)) {
                setForeground(Color.RED);
            } else if (text.getText().contains(LiveDataCache.SYMBOL_DOWN)) {
                setForeground(Color.GREEN);
            } else {
                setForeground(Color.BLACK);
            }
            return this;
        }
    }

    public static void main(String[] args) {
        for (int i = 0; i < 20; i++) {
            WindowUtil.createMsgFrame("A2201" + i + ": 50秒 上涨0.36%【823.0-826.0】看多");
        }
    }
}
