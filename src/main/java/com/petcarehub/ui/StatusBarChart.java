package com.petcarehub.ui;

import com.petcarehub.model.TaskStatus;

import javax.swing.*;
import java.awt.*;
import java.util.EnumMap;
import java.util.Map;

final class StatusBarChart extends JPanel {
    private Map<TaskStatus, Long> counts = new EnumMap<>(TaskStatus.class);

    StatusBarChart() {
        setPreferredSize(new Dimension(420, 220));
    }

    void setCounts(Map<TaskStatus, Long> counts) {
        this.counts = new EnumMap<>(counts);
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        try {
            int w = getWidth();
            int h = getHeight();
            int left = 55, right = 20, top = 25, bottom = 45;
            int chartW = Math.max(1, w - left - right);
            int chartH = Math.max(1, h - top - bottom);
            long max = Math.max(1L, counts.values().stream().mapToLong(Long::longValue).max().orElse(1));
            TaskStatus[] statuses = TaskStatus.values();
            int slot = chartW / statuses.length;
            int barW = Math.max(20, slot / 2);

            g2.drawLine(left, top, left, top + chartH);
            g2.drawLine(left, top + chartH, left + chartW, top + chartH);

            for (int i = 0; i < statuses.length; i++) {
                TaskStatus s = statuses[i];
                long value = counts.getOrDefault(s, 0L);
                int barH = (int) Math.round((value / (double) max) * (chartH - 10));
                int x = left + i * slot + (slot - barW) / 2;
                int y = top + chartH - barH;
                g2.fillRect(x, y, barW, barH);
                String label = s.name();
                int labelW = g2.getFontMetrics().stringWidth(label);
                g2.drawString(label, x + (barW - labelW) / 2, top + chartH + 18);
                String number = String.valueOf(value);
                int numberW = g2.getFontMetrics().stringWidth(number);
                g2.drawString(number, x + (barW - numberW) / 2, Math.max(top + 12, y - 4));
            }
        } finally {
            g2.dispose();
        }
    }
}
