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
    @Published var postState = PostState()
    
    @Published var commentsState = CommentsState()
    
    @Published var message = ""
    
    @Published var isShowingActionSheet = false
    
    @Published var isEditPostClick = false
    
    @Published var isMyPost = false
    
    @Published var isRemovePost = false// isShowingDetailView
    
    private let postRepository: PostRepository
    
    private let commentRepository: CommentRepository
    
    private var subscription = Set<AnyCancellable>()
    
    let postKey: String
    
    private func getPost(_ key: String) {
        postRepository.getPost(key).tryMap(getPostUseCase).sink(receiveCompletion: onReceive, receiveValue: onReceive).store(in: &subscription)
    }
    
    private func getComments(_ key: String) {
        commentRepository.getComments(key).tryMap(getCommentsUseCase).sink(receiveCompletion: onReceive, receiveValue: onReceive).store(in: &subscription)
    }
    
    private func getUserPostKeys(_ key: String) {
        postRepository.getUserPostKeys(key).sink(receiveCompletion: onReceive, receiveValue: onReceive).store(in: &subscription)
    }
    
    private func getPostUseCase(post: Post) -> Resource<Post> {
        do {
            return Resource.success(data: post)
        } catch {
            return Resource.error(message: error.localizedDescription)
        }
    }
    
    private func getCommentsUseCase(comments: [Comment]) -> Resource<[Comment]> {
        do {
            return Resource.success(data: comments)
        } catch {
            return Resource.error(message: error.localizedDescription)
        }
    }
    
    private func onReceive(_ result: Resource<Post>) {
        switch result.state {
        case .Success:
            self.postState = PostState(post: result.data)
        case .Error:
            self.postState = PostState(error: result.message ?? "An unexpected error occured")
        case .Loading:
            self.postState = PostState(isLoading: true)
        }
    }
    
    private func onReceive(_ result: Resource<[Comment]>) {
        switch result.state {
        case .Success:
            self.commentsState = CommentsState(comments: result.data ?? [])
        case .Error:
            self.commentsState = CommentsState(error: result.message ?? "An unexpected error occured")
        case .Loading:
            self.commentsState = CommentsState(isLoading: true)
        }
    }
    
    private func onReceive(_ keys: [String]) {
        isMyPost = keys.contains(postKey)
    }
    
    private func onAddCommentReceive(_ result: DatabaseReference) {
        message = ""
    }
    
    private func onRemovePostReceive(_ result: DatabaseReference) {
        isRemovePost = true
        print("onRemovePost: \(result)")
    }
    
    func onReceive(_ completion: Subscribers.Completion<Error>) {
        switch completion {
        case .finished:
            break
        case .failure:
            break
        }
    }
    
    func removePost() {
        postRepository.removePost(postKey).sink(receiveCompletion: onReceive, receiveValue: onRemovePostReceive).store(in: &subscription)
    }
    
    func addComment() {
        guard !message.isEmpty else {
            return
        }
        commentRepository.addComment(postKey, message).sink(receiveCompletion: onReceive, receiveValue: onAddCommentReceive).store(in: &subscription)
    }
    
    /*func onEvent(_ event: PostDetailEvent) {
        
    }*/
    
    init(_ postRepository: PostRepository, _ commentRepository: CommentRepository, _ key: String) {
        self.postRepository = postRepository
        self.commentRepository = commentRepository
        self.postKey = key
        
        getPost(key)
        getComments(key)
        getUserPostKeys(key)
    }
    
    deinit {
        subscription.removeAll()
    }
    
    struct PostState {
        var isLoading: Bool = false
        
        var post: Post? = nil
        
        var error: String = ""
    }
    
    struct CommentsState {
        var isLoading: Bool = false
        
        var comments: [Comment] = []
        
        var error: String = ""
    }
}

enum PostDetailEvent {
    case DeletePost(post: Post), OnDoneChange(post: Post, isDone: Bool)
}
