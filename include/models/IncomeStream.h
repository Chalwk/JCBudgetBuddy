#pragma once

#include <QString>
#include <QDate>
#include <QJsonObject>
#include <optional>
#include "utils/FinanceUtils.h"

struct IncomeStream {
    QString name;
    double amount = 0.0;
    IncomeFrequency frequency = IncomeFrequency::Weekly;
    QDate startDate;
    std::optional<QDate> endDate;
    bool active = true;
    QString notes;

    bool isCurrentlyActive() const;
    double weeklyAmount() const;
    QJsonObject toJson() const;
    static IncomeStream fromJson(const QJsonObject& obj);
};
