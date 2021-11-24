package com.guru.future.common.utils;

import com.google.common.base.Strings;
import com.guru.future.common.cache.LiveDataCache;
import com.guru.future.common.entity.vo.FutureLiveVO;
import lombok.extern.slf4j.Slf4j;

import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
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
public class WindowUtil {
    private static JFrame frame = null;
    private static DefaultListModel<JLabel> priceModel = new DefaultListModel<>();
    private static DefaultListModel<JLabel> highTopModel = new DefaultListModel<>();
    private static DefaultListModel<JLabel> lowTopModel = new DefaultListModel<>();

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

    public static void createMsgFrame(String overview, String content) {
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
            String frameTitle = "Trade Log【" + overview + "】";
            if (frame == null) {
                frame = new JFrame(frameTitle);
                frame.setLayout(new FlowLayout());
                frame.setBounds(0, 1000, 510, 438);
                frame.addWindowListener(new WindowAdapter() {
                    @Override
                    public void windowClosing(WindowEvent e) {
                        frame.dispose();
                        frame.getContentPane().removeAll();
                    }
                });
                refreshContentPane();
                frame.setVisible(true);
            } else {
                frame.setTitle(frameTitle);
                refreshContentPane();
            }
            frame.validate();
        } catch (Exception e) {
            log.error("create trade log frame failed, error={}", e);
        }
    }

    private static void refreshContentPane() {
        JList list = new JList(priceModel);
        list.setCellRenderer(new MyListCellRenderer());
        JScrollPane pane = new JScrollPane(list);
        pane.setPreferredSize(new Dimension(500, 130));
        Container c = frame.getContentPane();
        c.removeAll();
        c.add(pane);

        JList highTopList = new JList(highTopModel);
        highTopList.setCellRenderer(new MyListCellRenderer());
        JScrollPane highTopPane = new JScrollPane(highTopList);
        highTopPane.setPreferredSize(new Dimension(500, 130));
        c.add(highTopPane);

        JList lowTopList = new JList(lowTopModel);
        lowTopList.setCellRenderer(new MyListCellRenderer());
        JScrollPane lowTopPane = new JScrollPane(lowTopList);
        lowTopPane.setPreferredSize(new Dimension(500, 130));
        c.add(lowTopPane);
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
            WindowUtil.createMsgFrame("多", "A2201" + i + ": 50秒 上涨0.36%【823.0-826.0】看多");
        }
    }
}
