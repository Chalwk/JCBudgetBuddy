// Copyright (c) 2026 Jericho Crosby (Chalwk).
// Licensed under the GPL License.

#pragma once

#include <QString>
#include <QJsonObject>
#include <QList>
#include "models/Payment.h"

struct PlanItem {
    QString name;
    QString description;
    double totalCost = 0.0;
    double deposit = 0.0;
    double weeklyPayment = 0.0;

    QList<Payment> payments;

    double remainingCost() const;
    double weeksToComplete() const;
    QString timeframeString() const;

    double totalPaid() const;
    double remainingAfterPayments() const;
    double progressPercentage() const;
    QDate estimatedCompletionDate() const;
    void addPayment(const Payment& payment);

    QJsonObject toJson() const;
    static PlanItem fromJson(const QJsonObject& obj);
};