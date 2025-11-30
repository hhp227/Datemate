//
//  UserLocalDataSource.swift
//  DateMate
//
//  Created by 홍희표 on 2025/11/30.
//

import Foundation
import Combine

final class UserLocalDataSource {
    private let userDefaults = UserDefaults.standard
    
    private let preferenceKey = "preference_key"

    private(set) var preferenceSubject: CurrentValueSubject<Preference, Never>

    var userPublisher: AnyPublisher<UserCache?, Never> {
        preferenceSubject
            .map { $0.userCache }
            .eraseToAnyPublisher()
    }

    private init() {
        let currentPreference = UserLocalDataSource.loadPreference(from: UserDefaults.standard)
        preferenceSubject = CurrentValueSubject<Preference, Never>(currentPreference)

        // UserDefaults 변경 감지
        NotificationCenter.default.addObserver(
            forName: UserDefaults.didChangeNotification,
            object: nil,
            queue: .main
        ) { [weak self] _ in
            guard let self else { return }
            let newPref = UserLocalDataSource.loadPreference(from: self.userDefaults)
            self.preferenceSubject.send(newPref)
        }
    }

    private static func loadPreference(from defaults: UserDefaults) -> Preference {
        if let data = defaults.data(forKey: "preference_key"),
           let pref = try? JSONDecoder().decode(Preference.self, from: data) {
            return pref
        }
        return Preference()
    }

    func storeUser(_ user: UserCache?) {
        var pref = preferenceSubject.value
        pref.userCache = user

        if let encoded = try? JSONEncoder().encode(pref) {
            userDefaults.set(encoded, forKey: preferenceKey)
        }
        preferenceSubject.send(pref)
    }
    
    // Singleton
    static let shared = UserLocalDataSource()
}
