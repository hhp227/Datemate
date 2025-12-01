//
//  ChatRoomViewModel.swift
//  DateMate
//
//  Created by 홍희표 on 2025/12/01.
//

import Foundation
import Combine

class ChatRoomViewModel: ObservableObject {
    private let userRepository: UserRepository
    
    private let postRepository: PostRepository
    
    private var cancellables = Set<AnyCancellable>()
    
    @Published var uiState = MyProfileUiState()
    
    init(_ userRepository: UserRepository, _ postRepository: PostRepository) {
        self.userRepository = userRepository
        self.postRepository = postRepository
    }
}
