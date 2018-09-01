package cn.myperf4j.ext.metric.processor.logger;

import cn.myperf4j.base.metric.JvmGCMetrics;
import cn.myperf4j.base.metric.formatter.JvmGCMetricsFormatter;
import cn.myperf4j.base.metric.formatter.impl.DefaultJvmGCMetricsFormatter;
import cn.myperf4j.base.metric.processor.JvmGCMetricsProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by LinShunkang on 2018/8/25
 */
public class LoggerJvmGCMetricsProcessor implements JvmGCMetricsProcessor {

    private Logger logger = LoggerFactory.getLogger(LoggerJvmGCMetricsProcessor.class);

    private ConcurrentHashMap<Long, List<JvmGCMetrics>> metricsMap = new ConcurrentHashMap<>(8);

    private JvmGCMetricsFormatter metricsFormatter = new DefaultJvmGCMetricsFormatter();

    @Override
    public void beforeProcess(long processId, long startMillis, long stopMillis) {
        metricsMap.put(processId, new ArrayList<JvmGCMetrics>(1));
    }

    @Override
    public void process(JvmGCMetrics metrics, long processId, long startMillis, long stopMillis) {
        List<JvmGCMetrics> metricsList = metricsMap.get(processId);
        if (metricsList != null) {
            metricsList.add(metrics);
        } else {
            logger.error("LoggerJvmGCMetricsProcessor.process(" + processId + ", " + startMillis + ", " + stopMillis + "): metricsList is null!!!");
        }
    }

    @Override
    public void afterProcess(long processId, long startMillis, long stopMillis) {
        try {
            List<JvmGCMetrics> metricsList = metricsMap.get(processId);
            if (metricsList != null) {
                logger.info(metricsFormatter.format(metricsList, startMillis, stopMillis));
            } else {
                logger.error("LoggerJvmGCMetricsProcessor.afterProcess(" + processId + ", " + startMillis + ", " + stopMillis + "): metricsList is null!!!");
            }
        } finally {
            metricsMap.remove(processId);
        }
    }
}