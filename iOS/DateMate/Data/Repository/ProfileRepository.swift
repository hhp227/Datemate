//
//  ProfileRepository.swift
//  DateMate
//
//  Created by 홍희표 on 2025/12/03.
//

import Foundation
import Combine

class ProfileRepository {
    private let profileRemoteDataSource: ProfileRemoteDataSource
    
    func getTodayRecommendations(userId: String) -> AnyPublisher<Resource<[Profile]>, Never> {
        return profileRemoteDataSource.getTodayRecommendations(userId: userId).asResource()
    }
    
    private init(_ profileRemoteDataSource: ProfileRemoteDataSource) {
        self.profileRemoteDataSource = profileRemoteDataSource
    }
    
    private static var instance: ProfileRepository? = nil
    
    static func getInstance(profileRemoteDataSource: ProfileRemoteDataSource) -> ProfileRepository {
        if let instance = self.instance {
            return instance
        } else {
            let profileRepository = ProfileRepository(profileRemoteDataSource)
            self.instance = profileRepository
            return profileRepository
        }
    }
}
