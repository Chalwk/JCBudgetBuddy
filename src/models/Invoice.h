// Copyright (c) 2026 Jericho Crosby (Chalwk).
// Licensed under the GPL License.

#pragma once

#include <QString>
#include <QList>
#include <QJsonObject>
#include "models/Payment.h"

struct Invoice {
    QString number;
    double total = 0.0;
    QList<Payment> payments;

    double balance() const;
    QJsonObject toJson() const;
    static Invoice fromJson(const QJsonObject& obj);
};
