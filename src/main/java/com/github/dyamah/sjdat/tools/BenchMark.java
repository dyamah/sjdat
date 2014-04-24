package com.github.dyamah.sjdat.tools;

import java.io.PrintStream;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.MemoryType;
import java.lang.management.MemoryUsage;
import java.text.DecimalFormat;

public class BenchMark {
    private static final DecimalFormat DFORMAT = new DecimalFormat(".#");
    private static final String HEADER = "     -- State --\t    time [ms]\t  memory [kb]";
    private long peek_memory_;
    private long total_time_ ;
    private long start_memory_ ;
    private long stop_memory_ ;
    private long start_time_;
    private long stop_time_;
    private PrintStream out_ ;

    private long getPeakMemory() {
        long peak = 0;
        for (MemoryPoolMXBean mbean : ManagementFactory
                .getMemoryPoolMXBeans()) {
            if (mbean.getType() != MemoryType.HEAP)
                continue;
            if (!mbean.isValid())
                continue;
            MemoryUsage usage = mbean.getPeakUsage();
            peak += usage.getUsed();
        }
        return peak;
    }

    public BenchMark(){
        peek_memory_ = 0;
        total_time_  = 0;
        start_time_  = 0;
        stop_time_   = 0;

        out_ = System.err;
    }

    public void showHeader(){
        if (out_ == null)
            return ;
        out_.println(HEADER);
    }
    public void showTotal(){
        if (out_ == null)
            return ;
        out_.printf("%16s", "-- TOTAL --");
        out_.printf("\t%13s", total_time_);
        out_.printf("\t%13s", DFORMAT.format((double)peek_memory_ / 1024));
        out_.println();

    }

    public void start(String message){
        start_memory_ = getPeakMemory();
        start_time_ = System.currentTimeMillis();
        if (out_ == null)
            return ;
        String msg = message ;
        if (message.length() > 16)
            msg = message.substring(0, 16);
        out_.printf("%16s", msg);
    }

    public void stop(){
        stop_time_ = System.currentTimeMillis();
        stop_memory_ = getPeakMemory();
        if (stop_memory_ > peek_memory_)
            peek_memory_ = stop_memory_ ;

        total_time_ += (stop_time_ - start_time_);
        if (out_ == null)
            return ;

        out_.printf("\t%13s",(stop_time_ - start_time_));
        out_.printf("\t%13s", DFORMAT.format((double)(stop_memory_ - start_memory_) / 1024));
        out_.println();
    }

}
