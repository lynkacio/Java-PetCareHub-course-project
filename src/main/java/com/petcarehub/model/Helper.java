package com.petcarehub.model;

public final class Helper extends Person {
    private double creditScore;
    private boolean verified;
    private String certificatePath;

    public Helper(String userId, String firstName, String lastName, Gender gender, String passwordHash,
                  double creditScore, boolean verified, String certificatePath) {
        super(userId, firstName, lastName, gender, passwordHash, Role.HELPER);
        this.creditScore = Math.max(0, Math.min(10, creditScore));
        this.verified = verified;
        this.certificatePath = certificatePath == null ? "" : certificatePath;
    }

    public double getCreditScore() { return creditScore; }
    public boolean isVerified() { return verified; }
    public String getCertificatePath() { return certificatePath; }

    public void adjustCreditScore(double delta) {
        creditScore = Math.max(0, Math.min(10, creditScore + delta));
    }

    public void setVerified(boolean verified) { this.verified = verified; }
    public void setCertificatePath(String certificatePath) {
        this.certificatePath = certificatePath == null ? "" : certificatePath;
    }
}
