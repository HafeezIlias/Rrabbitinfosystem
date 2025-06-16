package com.suffhillrabbitfarm.rabbitinfosystem.models;

public class UserModel {
   private String uid;
   private String userName;
   private String email;
   private String phoneNumber;
   private String accountType;
   private String createdAt;
   private String updatedAt;
   private String status;
   private int rabbitCount;
   private int totalRabbitsEver;
   private String lastRabbitAdded;

   public UserModel() {
   }

   public UserModel(String uid, String userName, String email, String phoneNumber,
         String accountType, String createdAt) {
      this.uid = uid;
      this.userName = userName;
      this.email = email;
      this.phoneNumber = phoneNumber;
      this.accountType = accountType;
      this.createdAt = createdAt;
   }

   // Getters
   public String getUid() {
      return uid;
   }

   public String getUserName() {
      return userName;
   }

   public String getEmail() {
      return email;
   }

   public String getPhoneNumber() {
      return phoneNumber;
   }

   public String getAccountType() {
      return accountType;
   }

   public String getCreatedAt() {
      return createdAt;
   }

   public String getUpdatedAt() {
      return updatedAt;
   }

   public String getStatus() {
      return status;
   }

   public int getRabbitCount() {
      return rabbitCount;
   }

   public int getTotalRabbitsEver() {
      return totalRabbitsEver;
   }

   public String getLastRabbitAdded() {
      return lastRabbitAdded;
   }

   // Setters
   public void setUid(String uid) {
      this.uid = uid;
   }

   public void setUserName(String userName) {
      this.userName = userName;
   }

   public void setEmail(String email) {
      this.email = email;
   }

   public void setPhoneNumber(String phoneNumber) {
      this.phoneNumber = phoneNumber;
   }

   public void setAccountType(String accountType) {
      this.accountType = accountType;
   }

   public void setCreatedAt(String createdAt) {
      this.createdAt = createdAt;
   }

   public void setUpdatedAt(String updatedAt) {
      this.updatedAt = updatedAt;
   }

   public void setStatus(String status) {
      this.status = status;
   }

   public void setRabbitCount(int rabbitCount) {
      this.rabbitCount = rabbitCount;
   }

   public void setTotalRabbitsEver(int totalRabbitsEver) {
      this.totalRabbitsEver = totalRabbitsEver;
   }

   public void setLastRabbitAdded(String lastRabbitAdded) {
      this.lastRabbitAdded = lastRabbitAdded;
   }
}
