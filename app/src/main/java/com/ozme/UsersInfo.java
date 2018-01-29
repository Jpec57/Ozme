package com.ozme;

import com.google.firebase.database.IgnoreExtraProperties;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jpec on 11/01/18.
 */
@IgnoreExtraProperties
public class UsersInfo {
    @IgnoreExtraProperties
    public static class Filter{
        int age;
        int ageMax;
        int ageMin;
        boolean femme;
        boolean homme;
        boolean flash;
        boolean match;
        int maxDistance;
        boolean messages;
        String sexuality;
        int timelineMode;

        Filter(int age, int ageMax, int ageMin, boolean femme, boolean homme, boolean flash, boolean match, int maxDistance,
               boolean messages, String sexuality, int timelineMode){
            this.age=age;
            this.ageMax=ageMax;
            this.ageMin=ageMin;
            this.femme=femme;
            this.homme=homme;
            this.flash=flash;
            this.match=match;
            this.maxDistance=maxDistance;
            this.messages=messages;
            this.sexuality=sexuality;
            this.timelineMode=timelineMode;

        }

        Filter(){

        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }

        public int getAgeMax() {
            return ageMax;
        }

        public void setAgeMax(int ageMax) {
            this.ageMax = ageMax;
        }

        public int getAgeMin() {
            return ageMin;
        }

        public void setAgeMin(int ageMin) {
            this.ageMin = ageMin;
        }

        public boolean isFemme() {
            return femme;
        }

        public void setFemme(boolean femme) {
            this.femme = femme;
        }

        public boolean isHomme() {
            return homme;
        }

        public void setHomme(boolean homme) {
            this.homme = homme;
        }

        public boolean isFlash() {
            return flash;
        }

        public void setFlash(boolean flash) {
            this.flash = flash;
        }

        public boolean isMatch() {
            return match;
        }

        public void setMatch(boolean match) {
            this.match = match;
        }

        public int getMaxDistance() {
            return maxDistance;
        }

        public void setMaxDistance(int maxDistance) {
            this.maxDistance = maxDistance;
        }

        public boolean isMessages() {
            return messages;
        }

        public void setMessages(boolean messages) {
            this.messages = messages;
        }

        public String getSexuality() {
            return sexuality;
        }

        public void setSexuality(String sexuality) {
            this.sexuality = sexuality;
        }

        public int getTimelineMode() {
            return timelineMode;
        }

        public void setTimelineMode(int timelineMode) {
            this.timelineMode = timelineMode;
        }
    }
    @IgnoreExtraProperties
    public static class Users{
        String birthday;
        String challengeTitle;
        String code;
        String description;
        Filter filter;
        String gender;
        List<Boolean> hobbies;
        String ithink;
        String job;
        JSONObject location;
        List<String> photos;
        int preference1;
        int preference2;
        int preference3;
        JSONObject stats;
        String username;
        List<Long> messagers;
        long lastSeen;

        public Users(String birthday, String challengeTitle, String code, String description, Filter filter, String gender, List<Boolean> hobbies, String ithink, String job, JSONObject location, List<String> photos, int preference1, int preference2, int preference3, JSONObject stats, String username, List<Long> messagers, long lastSeen) {
            this.birthday = birthday;
            this.challengeTitle = challengeTitle;
            this.code = code;
            this.description = description;
            this.filter = filter;
            this.gender = gender;
            this.hobbies = hobbies;
            this.ithink = ithink;
            this.job = job;
            this.location = location;
            this.photos = photos;
            this.preference1 = preference1;
            this.preference2 = preference2;
            this.preference3 = preference3;
            this.stats = stats;
            this.username = username;
            this.messagers = messagers;
            this.lastSeen=lastSeen;
        }


        Users(){

        }

        public void setPhotos(List<String> photos) {
            this.photos = photos;
        }

        public List<Long> getMessagers() {
            return messagers;
        }

        public void setMessagers(List<Long> messagers) {
            this.messagers = messagers;
        }

        public Filter getFilter() {
            return filter;
        }

        public void setFilter(Filter filter) {
            this.filter = filter;
        }

        public String getBirthday() {
            return birthday;
        }

        public void setBirthday(String birthday) {
            this.birthday = birthday;
        }

        public String getChallengeTitle() {
            return challengeTitle;
        }

        public void setChallengeTitle(String challengeTitle) {
            this.challengeTitle = challengeTitle;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getGender() {
            return gender;
        }

        public void setGender(String gender) {
            this.gender = gender;
        }

        public List<Boolean> getHobbies() {
            return hobbies;
        }

        public void setHobbies(List<Boolean> hobbies) {
            this.hobbies = hobbies;
        }

        public String getIthink() {
            return ithink;
        }

        public void setIthink(String ithink) {
            this.ithink = ithink;
        }

        public String getJob() {
            return job;
        }

        public void setJob(String job) {
            this.job = job;
        }

        public JSONObject getLocation() {
            return location;
        }

        public void setLocation(JSONObject location) {
            this.location = location;
        }

        public List<String> getPhotos() {
            return photos;
        }

        public int getPreference1() {
            return preference1;
        }

        public void setPreference1(int preference1) {
            this.preference1 = preference1;
        }

        public int getPreference2() {
            return preference2;
        }

        public void setPreference2(int preference2) {
            this.preference2 = preference2;
        }

        public int getPreference3() {
            return preference3;
        }

        public void setPreference3(int preference3) {
            this.preference3 = preference3;
        }

        public JSONObject getStats() {
            return stats;
        }

        public void setStats(JSONObject stats) {
            this.stats = stats;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public long getLastSeen() {
            return lastSeen;
        }

        public void setLastSeen(long lastSeen) {
            this.lastSeen = lastSeen;
        }
    }

}
