//
//  PostDetailViewModel.swift
//  DateMate
//
//  Created by 홍희표 on 2021/12/14.
//

import Foundation
import Combine
import FirebaseDatabase

class PostDetailViewModel: ObservableObject {
    private let postRepository: PostRepository
    
    private let commentRepository: CommentRepository
    
    private var subscription = Set<AnyCancellable>()
    
    init(_ postRepository: PostRepository, _ commentRepository: CommentRepository) {
        self.postRepository = postRepository
        self.commentRepository = commentRepository
    }
    
    deinit {
        subscription.removeAll()
    }
}
