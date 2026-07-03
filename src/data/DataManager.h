// Copyright (c) 2026 Jericho Crosby (Chalwk).
// Licensed under the GPL License.

#pragma once

#include <QObject>
#include <QString>
#include "models/UserData.h"

class DataManager : public QObject {
    Q_OBJECT

public:
    static DataManager& instance();

    UserData& data();
    const UserData& data() const;

    QString filePath() const;
    QString lastError() const;

    void load();
    bool save(QString* errorMessage = nullptr) const;

signals:
    void dataChanged();

private:
    explicit DataManager(QObject* parent = nullptr);
    UserData m_data;
    QString m_lastError;
};
