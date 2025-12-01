//
//  MyProfileViewModel.swift
//  DateMate
//
//  Created by 홍희표 on 2025/12/01.
//

import Foundation
import Combine

class MyProfileViewModel: ObservableObject {
    private let userRepository: UserRepository
    
    private let postRepository: PostRepository
    
    @Published var uiState = MyProfileUiState()
    
    private var cancellables = Set<AnyCancellable>()
    
    private func loadUserProfile() {
        userRepository.remoteUserStatePublisher
            .compactMap { $0 }
            .sink { user in
                self.loadProfile(userId: user.uid)
                self.loadPosts(userId: user.uid)
            }
            .store(in: &cancellables)
    }
    
    private func loadProfile(userId: String) {
        userRepository.fetchUserProfile(userId: userId)
            .sink { resource in
                switch resource.state {
                case .Loading:
                    self.uiState.isLoading = true
                case .Success:
                    self.uiState.profile = resource.data!
                    self.uiState.isLoading = false
                case .Error:
                    self.uiState.message = resource.message
                    self.uiState.isLoading = false
                }
            }
            .store(in: &cancellables)
    }
    
    private func loadPosts(userId: String) {
        postRepository.fetchUserPosts(userId: userId)
            .sink { resource in
                switch resource.state {
                case .Loading:
                    self.uiState.isLoading = true
                case .Success:
                    let posts = resource.data ?? []
                    self.uiState.posts = posts
                    self.uiState.stats = UserStats(
                        postCount: posts.count,
                        followers: "2.5M",
                        following: "259"
                    )
                    self.uiState.isLoading = false
                case .Error:
                    self.uiState.message = resource.message
                    self.uiState.isLoading = false
                }
            }
            .store(in: &cancellables)
    }
    
    init(_ userRepository: UserRepository, _ postRepository: PostRepository) {
        self.userRepository = userRepository
        self.postRepository = postRepository
        
        loadUserProfile()
    }
}
