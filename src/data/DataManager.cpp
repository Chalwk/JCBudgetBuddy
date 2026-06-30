// Copyright (c) 2026 Jericho Crosby (Chalwk).
// Licensed under the GPL License.

#include "data/DataManager.h"

#include <QDir>
#include <QFile>
#include <QStandardPaths>
#include <QJsonDocument>
#include <QFileInfo>

DataManager& DataManager::instance() {
    static DataManager manager;
    return manager;
}

DataManager::DataManager(QObject* parent)
    : QObject(parent) {
    load();
}

UserData& DataManager::data() {
    return m_data;
}

const UserData& DataManager::data() const {
    return m_data;
}

QString DataManager::filePath() const {
    return QDir::homePath() + "/.JCBudgetBuddy/userdata.json";
}

QString DataManager::lastError() const {
    return m_lastError;
}

void DataManager::load() {
    m_lastError.clear();
    const QString path = filePath();
    QFile file(path);
    if (!file.exists()) {
        m_data = UserData{};
        return;
    }
    if (!file.open(QIODevice::ReadOnly)) {
        m_lastError = QStringLiteral("Unable to open data file for reading: %1").arg(file.errorString());
        return;
    }
    const QByteArray json = file.readAll();
    file.close();

    QJsonParseError parseError{};
    const QJsonDocument doc = QJsonDocument::fromJson(json, &parseError);
    if (parseError.error != QJsonParseError::NoError || !doc.isObject()) {
        m_lastError = QStringLiteral("Invalid JSON data: %1").arg(parseError.errorString());
        return;
    }

    m_data = UserData::fromJson(doc.object());
}

bool DataManager::save(QString* errorMessage) const {
    const QString path = filePath();
    QFileInfo info(path);
    QDir dir = info.dir();
    if (!dir.exists() && !dir.mkpath(".")) {
        if (errorMessage) *errorMessage = QStringLiteral("Unable to create data directory: %1").arg(dir.absolutePath());
        return false;
    }

    QFile file(path);
    if (!file.open(QIODevice::WriteOnly | QIODevice::Truncate)) {
        if (errorMessage) *errorMessage = QStringLiteral("Unable to open data file for writing: %1").arg(file.errorString());
        return false;
    }

    const QJsonDocument doc(m_data.toJson());
    file.write(doc.toJson(QJsonDocument::Indented));
    file.close();
    return true;
}
