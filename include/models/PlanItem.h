#pragma once

#include <QString>
#include <QJsonObject>

struct PlanItem {
    QString name;
    QString description;
    double totalCost = 0.0;
    double deposit = 0.0;
    double weeklyPayment = 0.0;

    double remainingCost() const;
    double weeksToComplete() const;
    QString timeframeString() const;
    QJsonObject toJson() const;
    static PlanItem fromJson(const QJsonObject& obj);
};
