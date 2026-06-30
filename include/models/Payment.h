#pragma once
#include <QDate>
#include <QJsonObject>

struct Payment {
    QDate date;
    double amount = 0.0;

    QJsonObject toJson() const;
    static Payment fromJson(const QJsonObject& obj);
};
