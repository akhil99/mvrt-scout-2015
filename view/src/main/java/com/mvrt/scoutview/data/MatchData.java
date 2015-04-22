package com.mvrt.scoutview.data;

import java.util.ArrayList;

/**
 * @author Akhil Palla
 */
public class MatchData implements Comparable<MatchData> {

    public int matchNo;
    int teamNo;

    public int stacksWithContainers;
    public int stacksWithoutContainers;
    public int totalStackHeight;

    public int feederTotes;
    public int landfillTotes;

    public int numStacks;
    public double avgStackHeight;
    public int stacksCapped;
    public int containersFromStep;
    public int autonContainersFromStep;
    public int autonYellowTotes;
    public int destroyedStacks;

    public int capping_rating;
    public int coop_rating;
    public int intake_rating;
    public int litter_rating;
    public int stacking_rating;

    public ArrayList<Stack> stacks;

    public MatchData(int matchNo, int teamNo){
        this.matchNo = matchNo;
        this.teamNo = teamNo;
        stacks = new ArrayList<>();
    }

    public void setStackData(String stacks){
        String[]stackArr = stacks.replaceAll("\\[", "").replaceAll("\\]", "").replaceAll(" ", "").split(",");
        for(String str:stackArr) {
            if(str.equals(""))continue;
            Stack stack = new Stack();
            if (str.substring(1).equals("c")){
                stacksWithContainers++;
                stack.capped = true;
            }
            else stacksWithoutContainers++;
            int height = Integer.parseInt("" + str.charAt(0));
            stack.height = height;
            this.stacks.add(stack);
            totalStackHeight += height;
            numStacks++;
        }
        avgStackHeight = (numStacks == 0)?0:totalStackHeight/numStacks;

    }

    public void setDestroyedStacks(String destroyed){
        String[] destroyedList = destroyed.replaceAll("\\[", "").replaceAll("\\]", "").split(",");
        for (String s : destroyedList) {
            if (!s.equals("")) destroyedStacks++;
        }
    }

    public void setCappedStacks(int capped){
        stacksCapped = capped;
    }

    public void setToteCount(int feeder, int landfill){
        landfillTotes = landfill;
        feederTotes = feeder;
    }

    public void setContainersFromStep(int cont){
        containersFromStep = cont;
    }
    public void setAutonContainersFromStep(int cont){
        autonContainersFromStep = cont;
    }

    public void setAutonTotes(int totes){
        autonYellowTotes = totes;
    }

    public void setCappingRating(int rate){
        capping_rating = rate;
    }

    public void setCoopRating(int rate){
        coop_rating = rate;
    }

    public void setStackingRating(int rate){
        stacking_rating = rate;
    }

    public void setLitterRating(int rate){
        litter_rating = rate;
    }

    public void setIntakeRating(int rate){
        intake_rating = rate;
    }

    @Override
    public int compareTo(MatchData o) {
        return this.matchNo - o.matchNo;
    }

    public static class Stack{
        public int height;
        public boolean capped;
    }

}
