package com.google.challenges;

import java.util.*;
import java.util.Map.Entry;

//full disclosure.  i needed an implementation of
//interval search in java
//i took what i needed from here
//https://github.com/kevinjdolan/intervaltree
public class Answer {
    public static void main(String[] args){
        int[][] meetings4 = {{0, 1}, {1, 2}, {2, 3}, {3, 5}, {4, 5}};
        int[][] meetings1 =  {{0, 1000000}, {42, 43}, {0, 1000000}, {42, 43}};
        System.out.println(answer(meetings4));
        System.out.println(answer(meetings1));
    }


    public static int answer(int[][] meetings) {
        // Your code goes here.
        List<Interval> intervalList = new ArrayList<Interval>();

        for(int i=0; i < meetings.length; i++){
            intervalList.add(new Interval(meetings[i][0], meetings[i][1]));
        }

        Collections.sort(intervalList, new Comparator<Interval>() {
            public int compare(Interval i1, Interval i2) {
                if (i1.size() == i2.size()) {
                    return 0;
                } else if (i1.size() > i2.size()) {
                    return 1;
                } else {
                    return -1;
                }
            }
        });

        return maxMeetings(intervalList, 1);
    }

    private static int maxMeetings(List<Interval> intervalList, int count){
        IntervalNode head = new IntervalNode(intervalList);
        Interval intervalWithLeastCollisions = intervalList.get(0);
        List<Interval> collisions = head.query(intervalWithLeastCollisions);
        intervalList.removeAll(collisions);

        if(intervalList.isEmpty()){
            return count;
        }
        else {
            return maxMeetings(intervalList, ++count);
        }
    }
}


 class IntervalNode{
    private SortedMap<Interval, List<Interval>> intervals;
    private Integer center;
    private IntervalNode leftNode;
    private IntervalNode rightNode;


    public IntervalNode(List<Interval> intervalList) {

        intervals = new TreeMap<Interval, List<Interval>>();

        SortedSet<Integer> endpoints = new TreeSet<Integer>();

        for(Interval interval: intervalList) {
            endpoints.add(interval.getStart());
            endpoints.add(interval.getEnd());
        }

        Integer median = getMedian(endpoints);
        center = median;

        List<Interval> left = new ArrayList<Interval>();
        List<Interval> right = new ArrayList<Interval>();

        for(Interval interval : intervalList) {
            if(interval.getEnd() < median)
                left.add(interval);
            else if(interval.getStart() > median)
                right.add(interval);
            else {
                List<Interval> posting = intervals.get(interval);
                if(posting == null) {
                    posting = new ArrayList<Interval>();
                    intervals.put(interval, posting);
                }
                posting.add(interval);
            }
        }

        if(left.size() > 0)
            leftNode = new IntervalNode(left);
        if(right.size() > 0)
            rightNode = new IntervalNode(right);
    }



    /**
     * Perform an interval intersection query on the node
     * @param target the interval to intersect
     * @return		   all intervals containing time
     */
    public List<Interval> query(Interval target) {
        List<Interval> result = new ArrayList<Interval>();

        for(Interval key : intervals.keySet()) {
            if(key.intersects(target))
                for(Interval interval : intervals.get(key))
                    result.add(interval);
            else if(key.getStart() > target.getEnd())
                break;
        }

        if(target.getStart() < center && leftNode != null)
            result.addAll(leftNode.query(target));
        if(target.getEnd() > center && rightNode != null)
            result.addAll(rightNode.query(target));
        return result;
    }


    private Integer getMedian(SortedSet<Integer> set) {
        int i = 0;
        int middle = set.size() / 2;
        for(Integer point : set) {
            if(i == middle)
                return point;
            i++;
        }
        return null;
    }
}


 class Interval implements Comparable<Interval>{
    private int start;
    private int end;

    public Interval(int start, int end) {
        this.start = start;
        this.end = end;
    }

    public int size(){
        return end - start;
    }
    public int getStart() {
        return start;
    }

    public int getEnd() {
        return end;
    }

    public boolean intersects(Interval other) {
        return other.getEnd() > start && other.getStart() < end;
    }

    public int compareTo(Interval other) {
        if(start < other.getStart())
            return -1;
        else if(start > other.getStart())
            return 1;
        else if(end < other.getEnd())
            return -1;
        else if(end > other.getEnd())
            return 1;
        else
            return 0;
    }


}

