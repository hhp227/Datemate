//
//  DiscoverViewModel.swift
//  DateMate
//
//  Created by 홍희표 on 2021/12/07.
//

import Foundation
import Combine

class DiscoverViewModel: ObservableObject {
    private let userRepository: UserRepository
    
    private let profileRepository: ProfileRepository
    
    var cancellables = Set<AnyCancellable>()
    
    @Published var uiState = DiscoverUiState()
    
    private func loadTodayRecommendations() {
        userRepository.remoteUserStatePublisher
            .compactMap { $0 }
            .flatMap { user in
                self.profileRepository.getTodayRecommendations(userId: user.uid)
            }
            .receive(on: DispatchQueue.main)
            .sink { resource in
                switch resource.state {
                case .Loading:
                    self.uiState.isLoading = true
                    self.uiState.message = nil
                case .Error:
                    self.uiState.isLoading = false
                    self.uiState.message = resource.message
                case .Success:
                    let list = resource.data ?? []
                    self.uiState.isLoading = false
                    self.uiState.todayRecommendations = list
                    self.uiState.message = list.isEmpty ? "No recommendations" : nil
                }
            }
            .store(in: &cancellables)
    }
    
    init(_ userRepository: UserRepository, _ profileRepository: ProfileRepository) {
        self.userRepository = userRepository
        self.profileRepository = profileRepository
        
        loadTodayRecommendations()
    }
}
