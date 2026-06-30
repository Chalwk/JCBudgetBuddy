#pragma once

#include <QString>
#include <QJsonObject>
#include "utils/FinanceUtils.h"

struct Bill {
    QString name;
    double amount = 0.0;
    BillFrequency frequency = BillFrequency::Weekly;
    QString paymentDay;
    BillPaymentMethod paymentMethod = BillPaymentMethod::Manual;
    QString notes;

    double weeklyAmount() const;
    QJsonObject toJson() const;
    static Bill fromJson(const QJsonObject& obj);
};
