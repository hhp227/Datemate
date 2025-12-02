//
//  ProfileRemoteDataSource.swift
//  DateMate
//
//  Created by 홍희표 on 2025/12/03.
//

import Foundation
import FirebaseFirestore
import Combine

class ProfileRemoteDataSource {
    private let firestore: Firestore
    
    private var cancellables = Set<AnyCancellable>()
    
    func getTodayRecommendations(userId: String, limit: Int = 5) -> AnyPublisher<[Profile], Error> {
        let today = getTodayKey()
        return Future<[Profile], Error> { promise in
            Task {
                do {
                    let docRef = self.firestore.collection("recommendations")
                        .document(userId)
                        .collection("daily")
                        .document(today)
                    let document = try await docRef.getDocument()
                    
                    if document.exists {
                        let profileIds = document.get("profileIds") as? [String] ?? []
                        var profiles: [Profile] = []
                        
                        for pid in profileIds {
                            let snap = try await self.firestore.collection("profiles")
                                .document(pid)
                                .getDocument()
                            
                            if var profile = try? snap.data(as: Profile.self) {
                                profile.id = pid
                                profiles.append(profile)
                            }
                        }
                        promise(.success(profiles))
                        return
                    } else {
                        let excluded = try await self.loadExcludedProfileIds(userId: userId)
                        let candidates = try await self.loadCandidateProfiles(userId: userId, excludedIds: excluded)
                        let chosen = Array(candidates.shuffled().prefix(limit))
                        
                        try await self.recordRecommendations(userId: userId, recommendedUids: chosen.compactMap { $0.id }, today: today)
                        promise(.success(chosen))
                    }
                } catch {
                    promise(.failure(error))
                }
            }
        }
        .eraseToAnyPublisher()
    }
    
    private func recordRecommendations(userId: String, recommendedUids: [String], today: String) async throws {
        let batch = firestore.batch()
        let docRef = firestore.collection("recommendations")
            .document(userId)
            .collection("daily")
            .document(today)
        let record: [String: Any] = [
            "profileIds": recommendedUids,
            "createdAt": Timestamp()
        ]
        
        batch.setData(record, forDocument: docRef)
        try await batch.commit()
    }
    
    private func loadExcludedProfileIds(userId: String) async throws -> Set<String> {
        let weekAgo = Timestamp(date: Date(timeIntervalSinceNow: -Double(Self.MIN_EXCLUSION_DAYS) * 86400))
        let snapshot = try await firestore.collection("recommendations")
            .document(userId)
            .collection("daily")
            .whereField("createdAt", isGreaterThan: weekAgo)
            .getDocuments()
        let ids = snapshot.documents.flatMap {
            $0.get("profileIds") as? [String] ?? []
        }
        return Set(ids)
    }
    
    private func loadCandidateProfiles(userId: String, excludedIds: Set<String>) async throws -> [Profile] {
        let myProfileDoc = try await firestore.collection("profiles").document(userId).getDocument()
        let myProfile = try? myProfileDoc.data(as: Profile.self)
        let gender: Gender = (myProfile?.gender == Gender.male.rawValue) ? .female : .male
        let snapshot = try await firestore.collection("profiles")
            .whereField("gender", isEqualTo: gender.rawValue)
            .limit(to: 100)
            .getDocuments()
        return snapshot.documents.compactMap { document in
            guard var profile = try? document.data(as: Profile.self) else { return nil }
            let uid = document.documentID
            profile.id = uid
            return (uid != userId && !excludedIds.contains(uid)) ? profile : nil
        }
    }
    
    private func getTodayKey() -> String {
        let formatter = DateFormatter()
        formatter.dateFormat = "yyyy-MM-dd"
        return formatter.string(from: Date())
    }
    
    private init(_ firestore: Firestore) {
        self.firestore = firestore
    }
    
    static let MIN_EXCLUSION_DAYS: Int = 7
    
    private static var instance: ProfileRemoteDataSource? = nil
    
    static func getInstance(firestore: Firestore) -> ProfileRemoteDataSource {
        if let instance = self.instance {
            return instance
        } else {
            let profileDataSource = ProfileRemoteDataSource(firestore)
            self.instance = profileDataSource
            return profileDataSource
        }
    }
}
