#include <QApplication>
#include <QFile>
#include <QStyleFactory>

#include "MainWindow.h"

static QString loadStyleSheet() {
    QFile file(":/style.qss");
    if (!file.open(QIODevice::ReadOnly | QIODevice::Text)) {
        return {};
    }
    return QString::fromUtf8(file.readAll());
}

int main(int argc, char *argv[]) {
    QApplication app(argc, argv);
    app.setStyle(QStyleFactory::create("Fusion"));

    const QString qss = loadStyleSheet();
    if (!qss.isEmpty()) {
        app.setStyleSheet(qss);
    }

    MainWindow window;
    window.show();

    return app.exec();
}
