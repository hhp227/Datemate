//
//  SignUpView.swift
//  DateMate
//
//  Created by 홍희표 on 2021/11/28.
//

import SwiftUI

struct SignUpView: View {
    @StateObject var viewModel: SignUpViewModel = DependencyContainer.instance.provideSignUpViewModel()
    
    @FocusState private var focusedField: Field?
    
    let onSignUpSuccess: () -> Void
    
    let onBackToSignIn: () -> Void
    
    var body: some View {
        let uiState = viewModel.uiState
        
        ScrollView {
            VStack(alignment: .center, spacing: 0) {
                Text("Create an Account")
                    .font(.title)
                    .padding(.bottom, 32)
                VStack(spacing: 20) {
                    EmailField(
                        value: uiState.email,
                        error: uiState.emailError,
                        onValueChange: viewModel.onEmailChange,
                        onSubmit: { focusedField = .password }
                    )
                    .focused($focusedField, equals: .email)
                    PasswordField(
                        value: uiState.password,
                        error: uiState.passwordError,
                        onValueChange: viewModel.onPasswordChange
                    )
                    .focused($focusedField, equals: .password)
                    .submitLabel(.next)
                    .onSubmit {
                        focusedField = .confirmPassword
                    }
                    PasswordField(
                        value: uiState.confirmPassword,
                        error: uiState.confirmPasswordError,
                        onValueChange: viewModel.onConfirmPasswordChange,
                        isConfirmPassword: true
                    )
                    .focused($focusedField, equals: .confirmPassword)
                    .submitLabel(.done)
                    .onSubmit {
                        if viewModel.uiState.isSignUpEnabled {
                            viewModel.signUp(uiState.email, uiState.password, uiState.confirmPassword)
                        }
                    }
                    Button(action: { viewModel.signUp(uiState.email, uiState.password, uiState.confirmPassword) }) {
                        if uiState.isLoading {
                            ProgressView().frame(width: 24, height: 24)
                        } else {
                            Text("Sign Up").foregroundColor(.white)
                        }
                    }
                    .frame(maxWidth: .infinity)
                    .frame(height: 52)
                    .background(uiState.isSignUpEnabled && !uiState.isLoading ? Color.blue : Color.gray.opacity(0.4))
                    .cornerRadius(16)
                    .disabled(!uiState.isSignUpEnabled || uiState.isLoading)
                    Button(action: onBackToSignIn) {
                        HStack(spacing: 4) {
                            Text("Already have an account?")
                            Text("Sign In")
                                .bold()
                        }
                    }
                    .padding(.top, 8)
                }
                .padding(.horizontal, 16)
            }
        }
        .onChange(of: uiState.isSignUpSuccess) { success in
            if success {
                viewModel.consumeSuccessEvent()
                onSignUpSuccess()
            }
        }
        .alert(item: Binding(
            get: { viewModel.uiState.errorMessage.map { ErrorMessage(id: UUID(), message: $0) } },
            set: { _ in viewModel.uiState.errorMessage = nil }
        )) { item in
            Alert(
                title: Text("오류"),
                message: Text(item.message),
                dismissButton: .default(Text("확인"), action: {
                    viewModel.uiState.errorMessage = nil
                })
            )
        }
    }
    
    enum Field {
        case name, email, password, confirmPassword
    }
}

struct ErrorMessage: Identifiable {
    let id: UUID
    let message: String
}

struct SignUpView_Previews: PreviewProvider {
    static var previews: some View {
        SignUpView(onSignUpSuccess: {}, onBackToSignIn: {})
    }
}
