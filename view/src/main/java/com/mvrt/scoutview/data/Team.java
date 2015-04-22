package com.mvrt.scoutview.data;

import java.text.DecimalFormat;
import java.util.TreeSet;

/**
 * @author Akhil Palla
 */
public class Team implements Comparable<Team> {

    private int teamNo;
    private String name = "";
    private String location = "";
    private String fullName = "";
    private String website = "";

    private double OPR = -1;
    private double avg = -1;
    private int rank = -1;

    //EXTENDED DATA:
    private int autoPts = -1;
    private int containerPts = -1;
    private int coopPts = -1;
    private int litterPts = -1;
    private int totePts = -1;
    private int gamesPlayed = -1;

    //SCOUTING DATA:
    private double avgStackHeight = -1;
    private double avgNumStacks = -1;

    TreeSet<MatchData> matches;

    public enum SortingType {SORT_TEAMNO, SORT_AVG, SORT_OPR};
    SortingType sortingType;

    public Team(int no){
        teamNo = no;
        sortingType = SortingType.SORT_TEAMNO;
        matches = new TreeSet<>();
    }


    public String getName(){ return name; }
    public int getTeamNo(){ return teamNo; }
    public int getRank(){ return rank; }
    public String getLocation(){ return location; }
    public String getFullName(){ return fullName; }
    public String getWebsite(){ return website; }
    public String getTitle(){ return teamNo + " - " + name; }
    public SortingType getSortingType(){ return sortingType; }

    public void setOPR(double opr){ OPR = opr; }
    public void setSortingType(SortingType t) { sortingType = t; }
    public void setName(String name){ this.name = name; }
    public void setWebsite(String website){ this.website = website; }
    public void setLocation(String loc){ location = loc; }
    public void setFullName(String fn){ fullName = fn; }

    public String getScoreString(){
        switch(sortingType){
            case SORT_OPR:
                return getOPRString();
            default:
                return "" + avg;
        }
    }

    public String getOPRString(){
        return new DecimalFormat("#.##").format(OPR);
    }

    public String getStatsString(){
        return "OPR " + getOPRString() + ", Avg " + avg;
    }

    public void setRankInfo(int rank, double avg){
        this.rank = rank;
        this.avg = avg;
    }

    public void setAdvancedRankInfo(int rank, double avg, int auto, int container, int coop, int litter, int tote, int played){
        this.rank = rank;
        this.avg = avg;
        autoPts = auto;
        containerPts = container;
        coopPts = coop;
        litterPts = litter;
        totePts = tote;
        gamesPlayed = played;
    }

    public double getAvg(){ return avg; }
    public int getAutoPts(){ return autoPts; }
    public int getAutoAvg(){ return autoPts/gamesPlayed; }
    public int getContainerPts(){ return containerPts; }
    public int getContainerAvg(){ return containerPts/gamesPlayed; }
    public int getCoopPts(){ return coopPts; }
    public int getCoopTimes(){ return coopPts/40; }
    public int getLitterPts(){ return litterPts; }
    public int getLitterAvg(){ return litterPts/gamesPlayed; }
    public int getTotePts(){ return totePts; }
    public int getToteAvg(){ return totePts/gamesPlayed; }
    public int getGamesPlayed(){ return gamesPlayed; }

    public boolean hasData(){
        return avg != -1  && !name.equals("");
    }

    public void addMatchData(MatchData data){
        matches.add(data);
        refreshData();
    }

    public void refreshData(){
        int totalHeight = 0;
        int stackCount = 0;
        int matchCount = 0;
        for(MatchData m:matches){
            totalHeight += m.totalStackHeight;
            stackCount += m.numStacks;
            matchCount++;
        }
        avgNumStacks = stackCount/(double)matchCount;
        avgStackHeight = totalHeight/(double)matchCount;
    }

    public TreeSet<MatchData> getMatchData(){
        return matches;
    }

    @Override
    public int compareTo(Team o) {
        switch(sortingType){
            case SORT_TEAMNO:
                return this.teamNo - o.teamNo;
            case SORT_AVG:
                return (int)(100 * (o.avg - this.avg));
            case SORT_OPR:
                return (int)(100 * (o.avg - this.avg));
            default:
                return 0;
        }
    }
}
